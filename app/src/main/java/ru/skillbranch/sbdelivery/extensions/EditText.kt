package ru.skillbranch.sbdelivery.extensions

import android.widget.EditText

fun EditText.setTextIfDifferent(newText: String) {
    if (text.toString() != newText) setText(newText)
}