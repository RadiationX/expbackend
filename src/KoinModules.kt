package ru.radiationx

import com.mysql.jdbc.Connection
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.log
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy
import ru.radiationx.api.BatchApiRouting
import ru.radiationx.api.route.*
import ru.radiationx.common.GMTDateSerializer
import ru.radiationx.data.datasource.FavoriteDbDataSource
import ru.radiationx.data.datasource.UserDbDataSource
import ru.radiationx.data.datasource.VoteDbDataSource
import ru.radiationx.data.entity.db.FavoritesTable
import ru.radiationx.data.entity.db.UsersTable
import ru.radiationx.data.entity.db.VotesTable
import ru.radiationx.data.repository.*
import ru.radiationx.domain.config.ServiceConfigHolder
import ru.radiationx.domain.config.SessionizeConfigHolder
import ru.radiationx.domain.helper.UserValidator
import ru.radiationx.domain.repository.*
import ru.radiationx.domain.usecase.*
import java.lang.Exception

const val DB_POOL = "database-pool"
const val SESSIONIZE_CLIENT = "sessionize-client"

fun serviceConfigModule(application: Application) = module(createdAtStart = true) {
    val config = application.environment.config
    val serviceConfig = config.config("service")
    val mode = serviceConfig.property("environment").getString()
    val adminSecret = serviceConfig.property("secret").getString()
    val production = mode == "production"
    application.log.info("Environment: $mode")
    single { ServiceConfigHolder(mode, production, adminSecret) }
}

fun sessionizeConfigModule(application: Application) = module(createdAtStart = true) {
    val config = application.environment.config
    val sessionizeConfig = config.config("sessionize")
    val sessionizeUrl = sessionizeConfig.property("url").getString()
    val oldSessionizeUrl = sessionizeConfig.property("oldUrl").getString()
    val sessionizeInterval = sessionizeConfig.property("interval").getString().toLong()
    single { SessionizeConfigHolder(sessionizeUrl, oldSessionizeUrl, sessionizeInterval) }
}

fun domainModule(application: Application) = module(createdAtStart = true) {
    single<UserValidator>()

    single<FavoriteUseCase>()
    single<FullInfoUseCase>()
    single<LiveVideoUseCase>()
    single<SessionizeUseCase>()
    single<TimeUseCase>()
    single<UserUseCase>()
    single<VoteUseCase>()
}

fun apiModule(application: Application) = module(createdAtStart = true) {
    single<ApiFavoriteRoute>()
    single<ApiFullInfoRoute>()
    single<ApiLiveVideoRoute>()
    single<ApiSessionizeRoute>()
    single<ApiTimeRoute>()
    single<ApiUsersRoute>()
    single<ApiVoteRoute>()

    single<BatchApiRouting>()
}

fun dataModule(application: Application) = module(createdAtStart = true) {
    single { FavoriteDbDataSource(get(named(DB_POOL)), get()) }
    single { UserDbDataSource(get(named(DB_POOL)), get()) }
    single { VoteDbDataSource(get(named(DB_POOL)), get()) }

    singleBy<FavoriteRepository, FavoriteRepositoryImpl>()
    singleBy<LiveVideoRepository, LiveVideoRepositoryImpl>()
    single<SessionizeRepository> {
        SessionizeRepositoryImpl(get(), get(named(SESSIONIZE_CLIENT)))
    }
    singleBy<TimeRepository, TimeRepositoryImpl>()
    singleBy<UserRepository, UserRepositoryImpl>()
    singleBy<VoteRepository, VoteRepositoryImpl>()
}

fun clientModule(application: Application) = module(createdAtStart = true) {
    val sessionizeClient = HttpClient {
        install(JsonFeature) {
            serializer = GsonSerializer {
                registerTypeAdapter(GMTDate::class.java, GMTDateSerializer)
            }
        }
    }
    single(named(SESSIONIZE_CLIENT)) { sessionizeClient }
}

fun dataBaseModule(application: Application) = module(createdAtStart = true) {
    val useDb = application.environment.config.config("database").property("use").getString()

    val database = when (useDb) {
        "mysql" -> initMySqlDatabase(application)
        "sqlite" -> initSQLiteDatabase(application)
        else -> throw Exception("Unknown db=$useDb")
    }

    //val dispatcher = newFixedThreadPoolContext(50, "database-pool")
    val dispatcher = Dispatchers.IO

    transaction(database) {
        SchemaUtils.create(
            UsersTable,
            FavoritesTable,
            VotesTable
        )
    }

    single(named(DB_POOL)) { dispatcher }
    single { database }
}

private fun initMySqlDatabase(application: Application): Database {
    val appConfig = application.environment.config.config("database").config("mysql")
    val url = appConfig.property("connection").getString()
    val user = appConfig.property("user").getString()
    val pass = appConfig.property("pass").getString()
    val poolSize = appConfig.property("poolSize").getString().toInt()
    application.log.info("Connecting to database at '$url'")

    val config = HikariConfig().apply {
        jdbcUrl = url
        maximumPoolSize = poolSize
        username = user
        password = pass
        addDataSourceProperty("cachePrepStmts", true)
        addDataSourceProperty("prepStmtCacheSize", 250)
        addDataSourceProperty("prepStmtCacheSqlLimit", 2048)
        addDataSourceProperty("useServerPrepStmts", true)
        addDataSourceProperty("useLocalSessionState", true)
        addDataSourceProperty("rewriteBatchedStatements", true)
        addDataSourceProperty("cacheResultSetMetadata", true)
        addDataSourceProperty("cacheServerConfiguration", true)
        addDataSourceProperty("elideSetAutoCommits", true)
        addDataSourceProperty("maintainTimeStats", false)
        validate()
    }
    return Database.connect(HikariDataSource(config))
}

private fun initSQLiteDatabase(application: Application): Database {
    val appConfig = application.environment.config.config("database").config("sqlite")
    val url = appConfig.property("connection").getString()
    val poolSize = appConfig.property("poolSize").getString().toInt()
    application.log.info("Connecting to database at '$url'")

    val config = HikariConfig().apply {
        jdbcUrl = url
        maximumPoolSize = poolSize
        driverClassName = "org.sqlite.JDBC"
        validate()
    }

    val database = Database.connect(HikariDataSource(config))
    TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    return database
}

