package ru.radiationx.domain.helper

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun LocalDate.asDate(): Date = Date.from(this.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())

fun LocalDateTime.asDate(): Date = Date.from(this.atZone(ZoneId.systemDefault()).toInstant())

fun Date.asLocalDate(): LocalDate =
    Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDate()

fun Date.asLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this.time).atZone(ZoneId.systemDefault()).toLocalDateTime()