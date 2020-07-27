package ru.skillbranch.sbdelivery.extensions

import java.text.SimpleDateFormat
import java.util.*


fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy") =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)