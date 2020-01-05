package ru.radiationx.common

import com.google.gson.annotations.SerializedName
import io.ktor.util.date.*

class ConferenceData(
    val allData: SessionizeData = SessionizeData(),
    val favorites: List<String> = emptyList(),
    val votes: List<VoteData> = emptyList(),
    val liveVideos: List<LiveVideo> = emptyList(),
    val votesCountRequired: Int
)

class SessionizeData(
    val sessions: List<SessionData> = emptyList(),
    val rooms: List<RoomData> = emptyList(),
    val speakers: List<SpeakerData> = emptyList(),
    val questions: List<QuestionData> = emptyList(),
    val categories: List<CategoryData> = emptyList(),
    val partners: List<PartnerData> = emptyList()
)

data class SessionData(
    val id: String,
    val isServiceSession: Boolean,
    val isPlenumSession: Boolean,
    val speakers: List<String>,
    @SerializedName("description") var descriptionText: String? = "",
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val title: String,
    val roomId: Int?,
    val questionAnswers: List<QuestionAnswerData> = emptyList(),
    val categoryItems: List<Int> = emptyList()
) {
    val displayTitle: String get() = title.trim()

    init {
        if (descriptionText == null) descriptionText = ""
    }
}

val SessionData.url: String
    get() = buildString {
        val day = startsAt.dayOfMonth
        append("https://kotlinconf.com/talks/")
        append("$day-dec/$id")
    }

class RoomData(
    val name: String,
    val id: Int,
    val sort: Int
)

class SpeakerData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val profilePicture: String?,
    val sessions: List<String>,
    val tagLine: String,
    val isTopSpeaker: Boolean,
    val bio: String,
    val fullName: String,
    val links: List<LinkData> = emptyList(),
    val categoryItems: List<Int> = emptyList(),
    val questionAnswers: List<QuestionAnswerData> = emptyList()
)

class QuestionData(
    val question: String,
    val id: Int,
    val sort: Int,
    val questionType: String
)

class CategoryData(
    val id: Int,
    val sort: Int,
    val title: String,
    val items: List<CategoryItemData> = emptyList()
)

class VoteData(
    val sessionId: String,
    val rating: RatingData? = null
)

class QuestionAnswerData(
    val questionId: Int,
    val answerValue: String
)

class LinkData(
    val linkType: String,
    val title: String,
    val url: String
)

class CategoryItemData(
    val name: String,
    val id: Int,
    val sort: Int
)

class PartnerData(
    val name: String,
    val logo: String,
    val description: String
)

class LiveVideo(val room: Int, val videoId: String)

/**
 * TODO: remove when serialization supports typeOf<EnumClass>()
 */
data class RatingData(val value: Int) {
    companion object {
        val BAD = RatingData(-1)
        val OK = RatingData(0)
        val GOOD = RatingData(1)

        fun valueOf(value: Int): RatingData =
            listOf(BAD, OK, GOOD).find { it.value == value } ?: error("Invalid rating value")
    }
}


fun Month.displayName(): String = name