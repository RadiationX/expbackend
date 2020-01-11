package ru.radiationx.app.domain.repository

import ru.radiationx.app.domain.entity.SessionizeData

interface SessionizeRepository {

    suspend fun getData(old:Boolean): SessionizeData

    suspend fun update()
}