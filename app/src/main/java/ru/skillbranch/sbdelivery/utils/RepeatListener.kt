package ru.skillbranch.sbdelivery.utils

import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener


class RepeatListener(
    private val initialInterval: Long,
    private val normalInterval: Long,
    private val releaseListener: (View) -> Unit = {},
    private val repeatAction: (View) -> Unit
) : OnTouchListener {

    init {
        require(!(initialInterval < 0 || normalInterval < 0)) { "negative interval" }
    }

    private val clickListener = View.OnClickListener { repeatAction(it) }

    private val handler: Handler = Handler()
    private var touchedView: View? = null
    private val handlerRunnable: Runnable = object : Runnable {
        override fun run() {
            if (touchedView?.isEnabled == true) {
                handler.postDelayed(this, normalInterval)
                clickListener.onClick(touchedView)
            } else { // if the view was disabled by the clickListener, remove the callback
                handler.removeCallbacks(this)
                touchedView?.isPressed = false
                touchedView = null
            }
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(handlerRunnable)
                handler.postDelayed(handlerRunnable, initialInterval)
                touchedView = view
                touchedView?.isPressed = true
                //view.performClick()
                clickListener.onClick(view)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(handlerRunnable)
                touchedView?.isPressed = false
                touchedView = null
                releaseListener(view)
                return true
            }
        }
        return false
    }
}