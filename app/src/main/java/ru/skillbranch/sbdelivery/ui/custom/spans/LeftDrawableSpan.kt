package ru.skillbranch.sbdelivery.ui.custom.spans

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting

class LeftDrawableSpan(
    private val drawable: Drawable,
    @Px
    private val padding: Float,
    @ColorInt
    private val textColor: Int
) : ReplacementSpan() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var iconSize = 0
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var textWidth = 0f
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var path = Path()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val textStart = x + iconSize + padding

        canvas.save()
        val trY = y + paint.descent() - drawable.bounds.bottom
        canvas.translate(x + padding/2f, trY)
        drawable.draw(canvas)
        canvas.restore()

        paint.forText {
            canvas.drawText(text, start, end, textStart, y.toFloat(), paint)
        }
    }


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (fm != null) {
            iconSize = (paint.descent() - paint.ascent()).toInt() //fm.descent - fm.ascent //fontSize
            drawable.setBounds(0, 0, iconSize, iconSize)
        }
        textWidth = paint.measureText(text.toString(), start, end)
        return (iconSize + padding + textWidth).toInt()
    }

    private inline fun Paint.forText(block: () -> Unit) {
        val oldColor = color

        color = textColor

        block()

        color = oldColor
    }
}