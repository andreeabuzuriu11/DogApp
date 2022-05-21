package com.buzuriu.dogapp.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.buzuriu.dogapp.R


class RatingBarWithNumber : RelativeLayout {

    var ratingBar: RatingBar? = null
    var numberTextView: TextView? = null


    constructor(var1: Context) : super(var1)
    constructor(var1: Context, var2: AttributeSet) : super(var1, var2)
    constructor(var1: Context, var2: AttributeSet, var3: Int) : super(var1, var2, var3)

    init {
        addView(inflate(context, R.layout.custom_rating_bar, null))
        ratingBar = this.findViewById(R.id.rating_bar)
        numberTextView = this.findViewById(R.id.star_number_text_view)
    }

    @SuppressLint("SetTextI18n")
    fun setText(value: Float) {
        if (value.isNaN()) {
            numberTextView!!.text = "No reviews yet"
        }
        else {
            numberTextView!!.text = "$value stars"
        }
    }

    fun getNumOfStars(): Float? {
        return ratingBar!!.rating.toFloat()
    }


}