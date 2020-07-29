package ru.skillbranch.sbdelivery.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.layout_counter_view.view.*
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.extensions.hideKeyboard
import ru.skillbranch.sbdelivery.extensions.setOnHoldListener
import ru.skillbranch.sbdelivery.extensions.setTextIfDifferent


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
    private var borderColor = 0
    private var cornerRadius = 0f
    private val defaultTextSize by lazy { resources.getDimension(R.dimen.hnp_def_text_size) }
    private var textSize = 0f
        get() {
            field = et_value.textSize
            return field
        }
        set(value) {
            et_value.setTextSize(TypedValue.COMPLEX_UNIT_PT, value)
            requestLayout()
            field = value
        }

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
        incIcon = context.resources.getDrawable(R.drawable.ic_chevron_right_black_24dp, context.theme)
        decIcon = context.resources.getDrawable(R.drawable.ic_chevron_left_black_24dp, context.theme)
        buttonTint = context.resources.getColor(android.R.color.transparent)
        viewBackground = context.resources.getDrawable(R.color.color_primary, context.theme)
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
            textSize = a.getDimension(R.styleable.HorizontalNumberPicker_hnp_textSize, defaultTextSize)
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
        //btn_inc.setOnClickListener {
        //    counterValue = et_value.text.toString().split(' ').first().toFloatOrNull() ?: counterValue
        //    val old = counterValue
        //    counterValue += incDecViewIncrement
        //    context.hideKeyboard(et_value)
        //    et_value.clearFocus()
        //    refreshCounter()
        //    onValueChangedListener?.onValueChange(this, old, counterValue)
        //}

        btn_inc.setOnHoldListener(repeatIntervalMillis, 400L, {
            onValueChangedListener?.onValueChange(this, value)
        }) { updateCounter(increment) }

        //btn_dec.setOnClickListener {
        //    counterValue = et_value.text.toString().split(' ').first().toFloatOrNull() ?: counterValue
        //    val old = counterValue
        //    counterValue -= incDecViewIncrement
        //    context.hideKeyboard(et_value)
        //    et_value.clearFocus()
        //    refreshCounter()
        //    onValueChangedListener?.onValueChange(this, old, counterValue)
        //}
        btn_dec.setOnHoldListener(repeatIntervalMillis, 400L, {
            onValueChangedListener?.onValueChange(this, value)
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

        et_value.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) et_value.setText("$value")
            else {
                updateCounter()
                onValueChangedListener?.onValueChange(this, value)
            }
        }
    }

    private fun applyValues() {
        if (incIcon != null) btn_inc.setImageDrawable(incIcon)
        if (decIcon != null) btn_dec.setImageDrawable(decIcon)
        if (viewBackground != null) root_view.background = viewBackground
        if (buttonTint != 0) {
            btn_inc.imageTintList = ColorStateList.valueOf(buttonTint)
            btn_dec.imageTintList = ColorStateList.valueOf(buttonTint)
        }
        if (borderWidth != -1f) applyBorder(width = borderWidth)
        if (borderColor != 0) applyBorder(color = borderColor)
        if (valueColor != 0) et_value.setTextColor(valueColor)
        updateUi()
    }

    private fun updateCounter(append: Int = 0) {
        value = et_value.text.toString().toIntOrNull() ?: value
        value += append
        context.hideKeyboard(et_value)
        et_value.clearFocus()
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
    }


    private fun applyBorder(
        @ColorInt color: Int = borderColor,
        width: Float = borderWidth,
        radius: Float = cornerRadius
    ) {

        val newColor = if (color != 0) color else resources.getColor(android.R.color.darker_gray)
        val newWidth = if (width != -1f) width.toInt() else getDimension(R.dimen.hnp_stroke_width).toInt()
        val newRadius = if (width != -1f) radius else getDimension(R.dimen.hnp_corner_radius)

        with (divider_left) {
            layoutParams = layoutParams.apply { this.width = newWidth }
            setBackgroundColor(newColor)
        }

        with (divider_right) {
            layoutParams = layoutParams.apply { this.width = newWidth }
            setBackgroundColor(newColor)
        }

        root_view?.background = GradientDrawable().apply {
            cornerRadius = newRadius
            setStroke(newWidth, newColor)
        }
        requestLayout()
    }


    private fun updateUi() {
        if (value < minValue) value = minValue
        if (value > maxValue) value = maxValue

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


    fun setSelectionDividerWidth(height: Int) {
        throw RuntimeException("Stub!")
    }

    fun getSelectionDividerWidth(): Int {
        throw RuntimeException("Stub!")
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

    fun setTextSize(type: Int, size: Float) {

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