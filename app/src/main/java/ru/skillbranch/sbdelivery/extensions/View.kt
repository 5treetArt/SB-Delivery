package ru.skillbranch.sbdelivery.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import ru.skillbranch.sbdelivery.utils.RepeatListener

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) {
    val currentParams = layoutParams as ViewGroup.MarginLayoutParams
    currentParams.setMargins(left, top, right, bottom)
    layoutParams = currentParams
}

fun View.setOnHoldListener(
    repeatInterval: Long,
    initialDelay: Long = 0L,
    onReleaseListener: (View) -> Unit = {},
    repeatAction: (View) -> Unit
) = setOnTouchListener(
    RepeatListener(initialDelay, repeatInterval, onReleaseListener, repeatAction)
)