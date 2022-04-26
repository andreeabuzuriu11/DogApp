package com.buzuriu.dogapp.bindingadapters

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.widget.Button
import androidx.databinding.BindingAdapter
import com.buzuriu.dogapp.enums.MeetingStateEnum

object ButtonBindingAdapter {

    @SuppressLint("SetTextI18n")
    @TargetApi(Build.VERSION_CODES.M)
    @BindingAdapter("cb_textViewButton")
    @JvmStatic
    fun Button.setProperTextView(meetingStateEnum: MeetingStateEnum?) {
        if (meetingStateEnum == MeetingStateEnum.JOINED)
            this.text = "LEAVE"
        else
            this.text = "JOIN"
    }
}