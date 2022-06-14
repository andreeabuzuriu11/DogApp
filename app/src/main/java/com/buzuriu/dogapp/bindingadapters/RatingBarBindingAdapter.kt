package com.buzuriu.dogapp.bindingadapters

import android.annotation.SuppressLint
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.components.RatingBarWithNumber
import kotlin.math.roundToInt

object RatingBarBindingAdapter {

    @BindingAdapter("numberOfStarsAttrChanged")
    @JvmStatic
    fun RatingBarWithNumber.setListener(listener: InverseBindingListener?) {
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        val textView = findViewById<TextView>(R.id.star_number_text_view)
        if (listener != null) {
            ratingBar.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onRatingChanged(
                    ratingBar: RatingBar?,
                    rating: Float,
                    fromUser: Boolean
                ) {
                    listener.onChange()
                    val number2digits = String.format("%.2f", rating)
                    textView.text = "$number2digits stars"
                }
            }
        }
    }


    @BindingAdapter("numberOfStars")
    @JvmStatic
    fun RatingBarWithNumber.setNumStars(value: Float) {
        val doubleValue = (value * 100.0).roundToInt() / 100.0
        this.setText(doubleValue.toFloat())
        this.ratingBar?.rating = doubleValue.toFloat()
    }

    @InverseBindingAdapter(attribute = "numberOfStars")
    @JvmStatic
    fun RatingBarWithNumber.getNumOfStars(): Float? {
        val doubleValue = (this.getNumOfStars()!!.toFloat() * 100.0).roundToInt() / 100.0
        return doubleValue.toFloat()
    }
}

