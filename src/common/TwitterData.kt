package ru.radiationx.common

class FeedData(
    val statuses: List<FeedPost> = emptyList()
)

class FeedPost(
    val id_str: String,
    val created_at: String,
    val text: String,
    val user: FeedUser,
    val entities: FeedEntities
)

class FeedUser(
    val id_str: String,
    val name: String,
    val profile_image_url_https: String,
    val screen_name: String
)

class FeedEntities(
    val media: List<FeedMedia> = emptyList()
)

class FeedMedia(
    val media_url: String? = null,
    val media_url_https: String? = null,
    val type: String? = null
)