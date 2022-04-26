package com.buzuriu.dogapp.bindingadapters

import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.components.DistanceSeekBar

object DistanceSeekBarBindingAdapter {

    @BindingAdapter("cb_progressAttrChanged")
    @JvmStatic
    fun DistanceSeekBar.setListener(listener: InverseBindingListener?) {
        val seekBar = findViewById<SeekBar>(R.id.my_custom_seekbar)
        if (listener != null) {
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    listener.onChange()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

        }

    }

    @BindingAdapter("cb_progress")
    @JvmStatic
    fun DistanceSeekBar.setProgress(value: Int) {
        this.setProgress(value as Int)
    }

    @InverseBindingAdapter(attribute = "cb_progress")
    @JvmStatic
    fun DistanceSeekBar.getProgress(): Int? {
        return this.getProgress()
    }
}