package com.buzuriu.dogapp.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.buzuriu.dogapp.R


class DistanceSeekBar : RelativeLayout {

    var seekBar: SeekBar? = null
    var textView: TextView? = null

    constructor(var1: Context) : super(var1)
    constructor(var1: Context, var2: AttributeSet) : super(var1, var2)
    constructor(var1: Context, var2: AttributeSet, var3: Int) : super(var1, var2, var3)

    init {
        addView(inflate(context, R.layout.custom_distance_seek_bar, null))
        seekBar = findViewById(R.id.my_custom_seekbar)
        textView = findViewById(R.id.my_custom_seekbar_text)
    }

    @SuppressLint("SetTextI18n")
    fun setText(value: Int) {
        val paint = Paint()
        val bounds = Rect()

        paint.textSize = textView!!.textSize

        textView!!.text = value.toString() + "km"
        val stringWidth = bounds.width()

        // set the seekBar width
        val width = (seekBar!!.width
                - seekBar!!.paddingLeft
                - seekBar!!.paddingRight)

        val thumbPos =
            seekBar!!.paddingLeft + width * seekBar!!.progress / seekBar!!.max

        // make the text move by dragging
        textView!!.translationX =
            thumbPos.toFloat() - stringWidth / 2 + seekBar!!.thumbOffset / 2
    }

    fun getProgress(): Int {
        return seekBar!!.progress
    }

    fun setProgress(value: Int) {
        seekBar!!.progress = value
        setText(value)
    }

}