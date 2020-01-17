package ru.radiationx.app.api.websocket

import ru.radiationx.app.api.base.WebSocketConverterException
import ru.radiationx.app.api.base.WebSocketTextEvent

class WebSocketTextEventConverter {

    private val regex = Regex("^#([\\w\\d\\s]+)#(?:#([\\w\\d\\s]+)#)?([\\s\\S]*)\$")
    private val regexGroupCount = 3

    fun createFrameText(event: WebSocketTextEvent): String = buildString {
        append("#")
        append(event.event)
        append("#")
        event.uuid?.also {
            append("#")
            append(it)
            append("#")
        }
        append(event.text)
    }

    fun parseFrameText(frameText: String): WebSocketTextEvent {
        val result = regex.find(frameText)
        if (result == null || result.groups.size != (regexGroupCount + 1)) {
            throw WebSocketConverterException("Wrong frame format")
        }
        val eventPart = result.groups[1]?.value ?: throw WebSocketConverterException("Not found event part")
        val uuidPart = result.groups[2]?.value
        val textPart = result.groups[3]?.value ?: throw WebSocketConverterException("Not found text part")
        return WebSocketTextEvent(eventPart, uuidPart, textPart)
    }

}