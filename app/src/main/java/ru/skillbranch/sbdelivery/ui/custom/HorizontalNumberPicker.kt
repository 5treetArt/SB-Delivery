package ru.skillbranch.sbdelivery.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.annotation.*
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
    private var strokeWidthRef = 0
    private var borderWidth = 0f
    private var borderColor = 0
    private var increment = 1
    private var minValue = Int.MIN_VALUE
    private var maxValue = Int.MAX_VALUE
    private var counterValueColor = 0
    private var repeatIntervalMillis = 100L
    private var defWidthValue = 0f
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
            incIcon = a.getDrawable(R.styleable.HorizontalNumberPicker_hnp_inc_icon)
            decIcon = a.getDrawable(R.styleable.HorizontalNumberPicker_hnp_dec_icon)
            viewBackground = a.getDrawable(R.styleable.HorizontalNumberPicker_hnp_background)
            buttonTint = a.getColor(R.styleable.HorizontalNumberPicker_hnp_button_color, 0)
            borderWidth = a.getDimension(R.styleable.HorizontalNumberPicker_hnp_border_width, 0f)
            borderColor = a.getColor(R.styleable.HorizontalNumberPicker_hnp_border_color, 0)
            value = a.getInt(R.styleable.HorizontalNumberPicker_hnp_value, 1)
            increment = a.getInt(R.styleable.HorizontalNumberPicker_hnp_increment, 1)
            counterValueColor = a.getColor(R.styleable.HorizontalNumberPicker_hnp_textColor, 0)
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
        }) {
            value = et_value.text.toString().split(' ').first().toIntOrNull() ?: value
            value += increment
            context.hideKeyboard(et_value)
            et_value.clearFocus()
            refreshCounter()
        }

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
        }) {
            value = et_value.text.toString().split(' ').first().toIntOrNull() ?: value
            value -= increment
            context.hideKeyboard(et_value)
            et_value.clearFocus()
            refreshCounter()
        }

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
                true
            } else false
        }

        et_value.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) et_value.setText("$value")
            else updateCounter()
        }
    }

    private fun updateCounter() {
        value = et_value.text.toString().toIntOrNull() ?: value
        context.hideKeyboard(et_value)
        et_value.clearFocus()
        refreshCounter()
        onValueChangedListener?.onValueChange(this, value)
    }

    private fun applyValues() {
        if (incIcon != null) btn_inc.setImageDrawable(incIcon)
        if (decIcon != null) btn_dec.setImageDrawable(decIcon)
        if (viewBackground != null) root_view.background = viewBackground
        if (buttonTint != 0) {
            btn_inc.imageTintList = ColorStateList.valueOf(buttonTint)
            btn_dec.imageTintList = ColorStateList.valueOf(buttonTint)
        }
        if (borderWidth != 0f) {
            this.setBorderWidth(borderWidth)
        }
        if (borderColor != 0) {
            initBorderColor(borderColor)
        }
        refreshCounter()
        if (counterValueColor != 0) {
            et_value.setTextColor(counterValueColor)
        }
    }

    fun setValue(counter: Int) {
        value = counter
        refreshCounter()
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

    fun setBorderWidth(@DimenRes strokeWidth: Int) {
        strokeWidthRef = strokeWidth
        val drawable = GradientDrawable()
        drawable.cornerRadius = getDimension(R.dimen.inc_dec_counter_view_corner_radius)
        drawable.setStroke(
            getDimension(strokeWidth).toInt(),
            resources.getColor(android.R.color.darker_gray)
        )
        root_view?.background = drawable
    }

    fun setIncrement(increment: Int) {
        this.increment = increment
        refreshCounter()
    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue
        refreshCounter()
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        refreshCounter()
    }

    fun getMinValue(): Int {
        return minValue
    }

    fun getMaxValue(): Int {
        return maxValue
    }

    fun setBorderColor(@ColorRes strokeColor: Int) {
        val drawable = GradientDrawable()
        drawable.cornerRadius = getDimension(R.dimen.inc_dec_counter_view_corner_radius)
        if (strokeWidthRef != 0) drawable.setStroke(
            getDimension(strokeWidthRef).toInt(),
            context.resources.getColor(strokeColor)
        ) else drawable.setStroke(
            getDimension(R.dimen.inc_dec_counter_view_stroke_width).toInt(),
            context.resources.getColor(strokeColor)
        )
        root_view?.background = drawable
    }

    private fun initBorderColor(color: Int) {
        val drawable = GradientDrawable()
        drawable.cornerRadius = getDimension(R.dimen.inc_dec_counter_view_corner_radius)
        if (defWidthValue != 0f) drawable.setStroke(
            defWidthValue.toInt(),
            color
        ) else drawable.setStroke(
            getDimension(R.dimen.inc_dec_counter_view_stroke_width).toInt(),
            color
        )
        root_view?.background = drawable
    }

    private fun refreshCounter() {
        if (value < minValue) value = minValue
        if (value > maxValue) value = maxValue
        refreshCounter(value)
    }

    private fun refreshCounter(counter: Int) {
        val text = formatter?.format(counter) ?: "$counter"
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

    private fun setBorderWidth(value: Float) {
        defWidthValue = value
        val drawable = GradientDrawable()
        drawable.cornerRadius = getDimension(R.dimen.inc_dec_counter_view_corner_radius)
        drawable.setStroke(value.toInt(), resources.getColor(android.R.color.darker_gray))
        root_view?.background = drawable
    }

    private fun runOnUiThread(action: () -> Unit) {
        handler.post(action)
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


    fun setSelectionDividerHeight(height: Int) {
        throw RuntimeException("Stub!")
    }

    fun getSelectionDividerHeight(): Int {
        throw RuntimeException("Stub!")
    }

    fun setTextColor(color: Int) {
        throw RuntimeException("Stub!")
    }

    fun setIconTint(color: Int) {
        throw RuntimeException("Stub!")
    }

    fun getTextColor(): Int {
        throw RuntimeException("Stub!")
    }


    fun setTextSize(size: Float) {
        throw RuntimeException("Stub!")
    }

    fun getTextSize(): Float {
        throw RuntimeException("Stub!")
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