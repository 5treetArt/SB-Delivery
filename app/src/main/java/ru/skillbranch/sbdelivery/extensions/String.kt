package ru.skillbranch.sbdelivery.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate(pattern: String = "HH:mm:ss dd.MM.yy"): Date? =
    SimpleDateFormat(pattern, Locale.getDefault())
        .parseOrNull(
            //FIX for parsing the Z literal
            this.replace("Z$".toRegex(), "+0000")
        )
