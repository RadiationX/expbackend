package ru.radiationx.domain.entity

data class Conference(
    val allData: SessionizeData,
    val favorites: List<Favorite>,
    val votes: List<Vote>,
    val liveVideos: List<LiveVideo>,
    val votesCountRequired: Int
)