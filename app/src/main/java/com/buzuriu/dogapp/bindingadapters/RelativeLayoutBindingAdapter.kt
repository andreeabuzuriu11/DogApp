package com.buzuriu.dogapp.bindingadapters

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.Build
import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.enums.MeetingStateEnum

object RelativeLayoutBindingAdapter {

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    @TargetApi(Build.VERSION_CODES.M)
    @BindingAdapter("backgroundBinding")
    @JvmStatic
    fun RelativeLayout.setBackground(meetingStateEnum: MeetingStateEnum?) {
        if (meetingStateEnum == MeetingStateEnum.JOINED)
            this.background = context.getDrawable(R.drawable.rounded_cell_joined)
        else
            this.background = context.getDrawable(R.drawable.rounded_cell)
    }
}