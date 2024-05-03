package com.buzuriu.dogapp.bindingadapters

import android.annotation.SuppressLint
import android.widget.Button
import androidx.databinding.BindingAdapter
import com.buzuriu.dogapp.enums.FriendshipStateEnum
import com.buzuriu.dogapp.enums.MeetingStateEnum

object ButtonBindingAdapter {

    @SuppressLint("SetTextI18n")
    @BindingAdapter("textViewButtonBinding")
    @JvmStatic
    fun Button.setProperTextView(meetingStateEnum: MeetingStateEnum?) {
        if (meetingStateEnum == MeetingStateEnum.JOINED)
            this.text = "LEAVE"
        else
            this.text = "JOIN"
    }


    @SuppressLint("SetTextI18n")
    @BindingAdapter("friendshipButtonBinding")
    @JvmStatic
    fun Button.setFriendshipTextView(friendshipStateEnum: FriendshipStateEnum?) {
        when (friendshipStateEnum) {
            FriendshipStateEnum.REQUESTED -> this.text = "FRIEND REQUEST SENT"
            FriendshipStateEnum.NOT_REQUESTED -> this.text = "SEND"
            FriendshipStateEnum.ACCEPTED -> this.text = "ALREADY FRIENDS"
        }
    }
}