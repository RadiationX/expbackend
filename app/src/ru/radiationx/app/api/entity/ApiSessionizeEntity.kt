package ru.radiationx.app.api.entity

import io.ktor.util.date.GMTDate
import ru.radiationx.domain.entity.*

data class ApiSessionResponse(
    val id: String,
    val isServiceSession: Boolean,
    val isPlenumSession: Boolean,
    val speakers: List<String>,
    var description: String? = "",
    val startsAt: GMTDate,
    val endsAt: GMTDate,
    val title: String,
    val roomId: Int?,
    val questionAnswers: List<QuestionAnswerData> = emptyList(),
    val categoryItems: List<Int> = emptyList()
)

data class ApiSessionizeResponse(
    val sessions: List<ApiSessionResponse> = emptyList(),
    val rooms: List<RoomData> = emptyList(),
    val speakers: List<SpeakerData> = emptyList(),
    val questions: List<QuestionData> = emptyList(),
    val categories: List<CategoryData> = emptyList(),
    val partners: List<PartnerData> = emptyList()
)