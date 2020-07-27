package ru.skillbranch.sbdelivery.extensions

import java.util.*

fun Float.format(digits: Int) = String.format(Locale.getDefault(), "%.${digits}f", this) //"%.${digits}f".format(this)