package com.buzuriu.dogapp.bindingadapters

import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.components.DistanceSeekBar

object DistanceSeekBarBindingAdapter {

    @BindingAdapter("sBProgressBindingAttrChanged")
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

    @BindingAdapter("sBProgressBinding")
    @JvmStatic
    fun DistanceSeekBar.setProgress(noOfKilometers: Int) {
        this.setProgress(noOfKilometers)
    }

    @InverseBindingAdapter(attribute = "sBProgressBinding")
    @JvmStatic
    fun DistanceSeekBar.getProgress(): Int {
        return this.getProgress()
    }
}