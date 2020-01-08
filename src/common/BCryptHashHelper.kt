package ru.radiationx.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import ru.radiationx.domain.helper.HashHelper

class BCryptHashHelper : HashHelper {

    override suspend fun check(original: String, hashed: String): Boolean = withContext(Dispatchers.Default) {
        BCrypt.checkpw(original, hashed)
    }

    override suspend fun hash(original: String): String = withContext(Dispatchers.Default) {
        if (original.length > 72) {
            // Just google this limit
            throw Exception("Limit hash length")
        }
        BCrypt.hashpw(original, BCrypt.gensalt())
    }
}