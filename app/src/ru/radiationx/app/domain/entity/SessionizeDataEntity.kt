package ru.radiationx.app.domain.entity

import com.google.gson.annotations.SerializedName
import io.ktor.util.date.GMTDate
import io.ktor.util.date.Month


data class SessionizeData(
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


fun Month.displayName(): String = name