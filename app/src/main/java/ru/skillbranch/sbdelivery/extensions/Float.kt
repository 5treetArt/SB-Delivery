package ru.skillbranch.sbdelivery.extensions

import java.text.DecimalFormat
import java.util.*

fun Float.format(digits: Int) = String.format(Locale.getDefault(), "%.${digits}f", this) //"%.${digits}f".format(this)

private val format = DecimalFormat("#.##")

fun Float.format() = format.format(this)