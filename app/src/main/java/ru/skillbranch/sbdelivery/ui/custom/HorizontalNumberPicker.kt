package ru.skillbranch.sbdelivery.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.graphics.alpha
import kotlinx.android.synthetic.main.layout_counter_view.view.*
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.extensions.hideKeyboard
import ru.skillbranch.sbdelivery.extensions.setOnHoldListener
import ru.skillbranch.sbdelivery.extensions.setTextIfDifferent
import kotlin.math.min


class HorizontalNumberPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var value = 0
    private var incIcon: Drawable? = null
    private var decIcon: Drawable? = null
    @ColorInt
    private var buttonTint = 0
    private var viewBackground: Drawable? = null
    private var borderWidth = -1f
    private val defaultBorderWidth by lazy { getDimension(R.dimen.hnp_stroke_width).toInt() }
    private var borderColor = 0
    private val defaultBorderColor by lazy { resources.getColor(android.R.color.darker_gray) }
    private var cornerRadius = 0f
    private val defaultCornerRadius by lazy { getDimension(R.dimen.hnp_corner_radius) }
    private var dividerWidth = -1f
    private val defaultDividerWidth by lazy { getDimension(R.dimen.hnp_stroke_width).toInt() }
    private var textSize = 0
    private val defaultTextSize by lazy { resources.getDimensionPixelSize(R.dimen.hnp_def_text_size) }
    private var increment = 1
    private var minValue = Int.MIN_VALUE
    private var maxValue = Int.MAX_VALUE
    private var valueColor = 0
    private var repeatIntervalMillis = 100L
    private var onValueChangedListener: OnValueChangeListener? = null
    private var editCompleteListener: (() -> Unit)? = null
    private var formatter: Formatter? = null

    init {
        inflate(context, R.layout.layout_counter_view, this)
        defValues(context)
        applyAttributes(attrs, defStyleAttr)
        applyListeners()
        applyValues()
    }

    //save state
    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.ssValue = value
        return savedState
    }

    //restore state
    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        if (state is SavedState) {
            setValue(state.ssValue)
        }
    }

    private fun defValues(context: Context) {
        incIcon = resources.getDrawable(R.drawable.ic_chevron_right_black_24dp, context.theme)
        decIcon = resources.getDrawable(R.drawable.ic_chevron_left_black_24dp, context.theme)
        buttonTint = resources.getColor(android.R.color.transparent)
        viewBackground = resources.getDrawable(R.color.color_primary, context.theme)
    }

    private fun applyAttributes(rawAttrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(rawAttrs, R.styleable.HorizontalNumberPicker, defStyle, 0)
        try {
            incIcon = a.getDrawable(R.styleable.HorizontalNumberPicker_hnp_incIcon)
            decIcon = a.getDrawable(R.styleable.HorizontalNumberPicker_hnp_decIcon)
            viewBackground = a.getDrawable(R.styleable.HorizontalNumberPicker_hnp_backgroundColor)
            buttonTint = a.getColor(R.styleable.HorizontalNumberPicker_hnp_buttonTint, 0)
            borderColor = a.getColor(R.styleable.HorizontalNumberPicker_hnp_borderColor, 0)
            borderWidth = a.getDimension(R.styleable.HorizontalNumberPicker_hnp_borderWidth, -1f)
            dividerWidth = a.getDimension(R.styleable.HorizontalNumberPicker_hnp_dividerWidth, -1f)
            textSize = a.getDimensionPixelSize(R.styleable.HorizontalNumberPicker_hnp_textSize, defaultTextSize)
            cornerRadius = a.getDimension(R.styleable.HorizontalNumberPicker_hnp_cornerRadius, -1f)
            value = a.getInt(R.styleable.HorizontalNumberPicker_hnp_value, 1)
            increment = a.getInt(R.styleable.HorizontalNumberPicker_hnp_increment, 1)
            valueColor = a.getColor(R.styleable.HorizontalNumberPicker_hnp_textColor, 0)
            minValue = a.getInt(R.styleable.HorizontalNumberPicker_hnp_minValue, Int.MIN_VALUE)
            maxValue = a.getInt(R.styleable.HorizontalNumberPicker_hnp_maxValue, Int.MAX_VALUE)
        } catch (e: Exception) {
            //Log.i("TAG_IncDecView", "init: " + e.localizedMessage)
        } finally {
            a.recycle()
        }
    }

    private fun applyListeners() {

        btn_inc.setOnHoldListener(repeatIntervalMillis, 400L, {
            onValueChangedListener?.onValueChange(this, value)
            et_value.clearFocus()
        }) { updateCounter(increment) }

        btn_dec.setOnHoldListener(repeatIntervalMillis, 400L, {
            onValueChangedListener?.onValueChange(this, value)
            et_value.clearFocus()
        }) { updateCounter(-increment) }

        //et_value.addTextChangedListener {
        //    var counterStr = it.toString().split(" ").firstOrNull()
        //    //Log.d("TAG_IncDecView", "counterStr: $counterStr")
        //    if (counterStr?.endsWith(',') == true || counterStr?.endsWith('.') == true) {
        //        counterStr += "0"
        //    }
        //    counterStr = counterStr?.replace(',', '.')
        //    counterValue = counterStr?.toFloatOrNull() ?: counterValue
        //    //Log.d("TAG_IncDecView", "counterValue: $counterValue")
        //    refreshCounter()
        //    onValueChangedListener?.onValueChange(this, counterValue)
        //}

        //et_value.setOnClickListener {
        //    et_value.setText("$counterValue")
        //}

        et_value.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                editCompleteListener?.invoke()
                updateCounter()
                onValueChangedListener?.onValueChange(this, value)
                true
            } else false
        }

        et_value.setOnFocusChangeListener { v, hasFocus ->
            Log.d("TAG_HNP", "isFocused: $hasFocus")
            if (hasFocus) (v as EditText).setText("$value")
            else {
                updateCounter()
                onValueChangedListener?.onValueChange(this, value)
            }
        }
    }

    private fun applyValues() {
        et_value.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        requestLayout()
        if (incIcon != null) btn_inc.setImageDrawable(incIcon)
        if (decIcon != null) btn_dec.setImageDrawable(decIcon)
        if (viewBackground != null) root_view.background = viewBackground
        if (buttonTint != 0) {
            btn_inc.imageTintList = ColorStateList.valueOf(buttonTint)
            btn_dec.imageTintList = ColorStateList.valueOf(buttonTint)
        }

        //TODO make borderBuilder?
        if (borderWidth != -1f) applyBorder(width = borderWidth)
        if (borderColor != 0) applyBorder(color = borderColor)
        if (cornerRadius != 0f) applyBorder(radius = cornerRadius)
        if (dividerWidth != -1f) setSelectionDividerWidth(dividerWidth)

        if (valueColor != 0) et_value.setTextColor(valueColor)
        updateUi()
    }

    private fun updateCounter(append: Int = 0) {
        Log.d("TAG_HNP", "val updating")
        value = et_value.text.toString().toIntOrNull() ?: value
        value += append
        context.hideKeyboard(et_value)
        updateUi()
    }

    fun setValue(counter: Int) {
        value = counter
        updateUi()
    }

    fun getValue() = value

    fun setCounterListener(onValueChangeListener: OnValueChangeListener) {
        this.onValueChangedListener = onValueChangeListener
    }

    fun setOnLongPressUpdateInterval(intervalMillis: Long) {
        repeatIntervalMillis = intervalMillis
    }

    fun setOnValueChangedListener(onChanged: (view: HorizontalNumberPicker, newValue: Int) -> Unit) {
        onValueChangedListener = object : OnValueChangeListener {
            override fun onValueChange(view: HorizontalNumberPicker, newValue: Int) =
                onChanged(view, newValue)
        }
    }

    fun setOnValueChangedListener(onValueChangedListener: OnValueChangeListener?) {
        this.onValueChangedListener = onValueChangedListener
    }

    fun setOnEditCompleteListener(onEditCompleteListener: () -> Unit) {
        editCompleteListener = onEditCompleteListener
    }

    fun setIncButtonIcon(@DrawableRes incButtonIcon: Int) {
        btn_inc.setImageResource(incButtonIcon)
    }

    fun setDecButtonIcon(@DrawableRes decButtonIcon: Int) {
        btn_dec.setImageResource(decButtonIcon)
    }

    fun setIncrement(increment: Int) {
        this.increment = increment
        updateUi()
    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue
        updateUi()
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        updateUi()
    }

    fun getMinValue(): Int {
        return minValue
    }

    fun getMaxValue(): Int {
        return maxValue
    }

    fun setCornerRadius(value: Float) {
        cornerRadius = value
        applyBorder(radius = cornerRadius)
    }

    fun setBorderWidth(value: Float) {
        borderWidth = value
        applyBorder(width = value)
    }

    fun setBorderColor(@ColorInt color: Int) {
        borderColor = color
        applyBorder(color = color)
        if (dividerWidth != -1f) setSelectionDividerWidth(dividerWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        /*        btn_dec.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(
                newRadius, newRadius, //Top left radius in px
                0f, 0f,         //Top right radius in px
                0f, 0f,         //Bottom right radius in px
                newRadius, newRadius  //Bottom left radius in px
            )
            //cornerRadius = newRadius
            setStroke(newWidth, newColor)
        }*/

    }

    private fun applyBorder(
        @ColorInt color: Int = borderColor,
        width: Float = borderWidth,
        radius: Float = cornerRadius
    ) {
        val newColor = if (color != 0) color else defaultBorderColor
        val newWidth = if (width != -1f) width.toInt() else defaultBorderWidth
        val newRadius = if (width != -1f) radius else defaultCornerRadius

        root_view?.background = GradientDrawable().apply {
            cornerRadius = newRadius
            setStroke(newWidth, newColor)
        }
        requestLayout()
    }


    private fun updateUi() {
        if (value < minValue) value = minValue
        if (value > maxValue) value = maxValue

        if (value == minValue) btn_dec.imageTintList = ColorStateList.valueOf(buttonTint).withAlpha(buttonTint.alpha / 2)
        else btn_dec.imageTintList = ColorStateList.valueOf(buttonTint)
        val text = formatter?.format(value) ?: "$value"
        et_value.setTextIfDifferent(text)
        //    String.format(
        //        Locale.getDefault(),
        //        "%s %s",
        //        counter.format(1),
        //        //format.format(counter.toDouble()),
        //        appendStr
        //    )
        //)
    }

    private fun getDimension(@DimenRes dimenID: Int): Float {
        return context.resources.getDimension(dimenID)
    }


    private fun runOnUiThread(action: () -> Unit) {
        handler.post(action)
    }

    fun setFormatter(formatter: Formatter) {
        this.formatter = formatter
    }

    /*
    override fun performClick(): Boolean {
        throw RuntimeException("Stub!")
    }

    override fun performLongClick(): Boolean {
        throw RuntimeException("Stub!")
    }
    */

    fun getWrapSelectorWheel(): Boolean {
        throw RuntimeException("Stub!")
    }

    fun setWrapSelectorWheel(wrapSelectorWheel: Boolean) {
        throw RuntimeException("Stub!")
    }


    fun getDisplayedValues(): Array<String>? {
        throw RuntimeException("Stub!")
    }

    fun setDisplayedValues(displayedValues: Array<String>) {
        throw RuntimeException("Stub!")
    }


    fun setSelectionDividerWidth(width: Float) {
        dividerWidth = width
        val color = if (borderColor != 0) borderColor else defaultBorderColor
        with (divider_left) {
            layoutParams = layoutParams.apply { this.width = width.toInt() }
            setBackgroundColor(color)
        }

        with (divider_right) {
            layoutParams = layoutParams.apply { this.width = width.toInt() }
            setBackgroundColor(color)
        }
        requestLayout()
        invalidate()
    }

    fun getSelectionDividerWidth(): Float {
        return dividerWidth
    }

    fun setTextColor(@ColorInt color: Int) {
        valueColor = color
        if (valueColor != 0)  et_value.setTextColor(valueColor)
    }

    fun setIconTint(@ColorInt color: Int) {
        buttonTint = color
        if (buttonTint != 0) {
            btn_inc.imageTintList = ColorStateList.valueOf(buttonTint)
            btn_dec.imageTintList = ColorStateList.valueOf(buttonTint)
        }
    }

    fun getTextColor(): Int {
        return valueColor
    }

    fun setTextSize(size: Float) {
        et_value.setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
        //textSize = et_value.textSize
        requestLayout()
    }

    fun getTextSize(): Float {
        return et_value.textSize
    }


    private class SavedState : BaseSavedState, Parcelable {
        var ssValue: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        constructor(src: Parcel) : super(src) {
            ssValue = src.readInt()
        }

        override fun writeToParcel(dst: Parcel, flags: Int) {
            super.writeToParcel(dst, flags)
            dst.writeInt(ssValue)
        }

        override fun describeContents() = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel) = SavedState(parcel)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }

    interface Formatter {
        fun format(value: Int): String
        fun unformat(strValue: String): Int?
    }


    interface OnValueChangeListener {

        @CallSuper
        fun onValueChange(view: HorizontalNumberPicker, oldValue: Int, newValue: Int) {
            onValueChange(view, newValue)
        }

        fun onValueChange(view: HorizontalNumberPicker, newValue: Int)

        //fun onValueChanged(value: Float)
        //@CallSuper
        //fun onAfterIncClick(value: Float) { onValueChanged(value) }
        //@CallSuper
        //fun onAfterDecClick(value: Float) { onValueChanged(value) }
    }
}