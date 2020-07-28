package ru.skillbranch.sbdelivery.extensions

import java.text.ParseException
import java.text.SimpleDateFormat

fun SimpleDateFormat.parseOrNull(source: String) =
    try {
        parse(source)
    } catch (e: ParseException) {
        null
    }