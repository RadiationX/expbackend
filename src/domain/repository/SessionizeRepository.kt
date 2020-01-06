package ru.radiationx.domain.repository

import ru.radiationx.domain.entity.SessionizeData

interface SessionizeRepository {

    suspend fun getData(old:Boolean): SessionizeData

    suspend fun update()
}