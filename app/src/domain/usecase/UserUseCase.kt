package ru.radiationx.domain.usecase

import ru.radiationx.domain.entity.User
import ru.radiationx.domain.repository.UserRepository

class UserUseCase(
    private val userRepository: UserRepository
) {

    suspend fun getUser(userId: Int): User? = userRepository.getUser(userId)

    suspend fun getAllUsers(): List<User> = userRepository.getAllUsers()

    suspend fun getAllUsersCount(): Int = userRepository.getAllUsersCount()

}