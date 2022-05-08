package com.buzuriu.dogapp.bindingadapters

import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.bindingadapters.RatingBarBindingAdapter.setNumStars
import com.buzuriu.dogapp.components.RatingBarWithNumber

object RatingBarBindingAdapter {

    @BindingAdapter("cb_nr_of_starsAttrChanged")
    @JvmStatic
    fun RatingBarWithNumber.setListener(listener: InverseBindingListener?) {
        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        val textView = findViewById<TextView>(R.id.star_number_text_view)
        if (listener != null) {
            ratingBar.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
                override fun onRatingChanged(
                    ratingBar: RatingBar?,
                    rating: Float,
                    fromUser: Boolean
                ) {
                    listener.onChange()
                    textView.text = "$rating stars"
                }
            }
        }
    }


    @BindingAdapter("cb_nr_of_stars")
    @JvmStatic
    fun RatingBarWithNumber.setNumStars(value: Float) {
        this.setText(value as Float)
        this.ratingBar?.rating = value
    }

    @InverseBindingAdapter(attribute = "cb_nr_of_stars")
    @JvmStatic
    fun RatingBarWithNumber.getNumOfStars(): Float? {
        return this.getNumOfStars()!!.toFloat()
    }
}

