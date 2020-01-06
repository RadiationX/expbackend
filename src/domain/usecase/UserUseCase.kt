package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.User
import ru.radiationx.domain.exception.BadRequest
import ru.radiationx.domain.repository.UserRepository
import java.time.Clock
import java.time.LocalDateTime

class UserUseCase(
    private val userRepository: UserRepository
) {

    suspend fun getUser(uuid: String): User? = userRepository.getUser(uuid)

    suspend fun createUser(uuid: String?, remote: String?): Boolean {
        uuid ?: throw BadRequest()
        remote ?: throw BadRequest()
        val timestamp = LocalDateTime.now(Clock.systemUTC())
        return userRepository.createUser(uuid, remote, timestamp)
    }

    suspend fun getAllUsers(): List<User> = userRepository.getAllUsers()

}