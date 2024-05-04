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
            FriendshipStateEnum.WAITING_FOR_YOUR_ACCEPT -> this.text = "WAITING FOR YOUR ACCEPT"
            FriendshipStateEnum.ACCEPTED -> this.text = "ALREADY FRIENDS"
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("enabledBasedOnFriendshipButtonBinding")
    @JvmStatic
    fun Button.setEnableBasedOnFriendship(friendshipStateEnum: FriendshipStateEnum?) {
        when (friendshipStateEnum) {
            FriendshipStateEnum.NOT_REQUESTED -> this.isEnabled = true
            FriendshipStateEnum.REQUESTED -> this.isEnabled = false
            FriendshipStateEnum.WAITING_FOR_YOUR_ACCEPT -> this.isEnabled = false
            FriendshipStateEnum.ACCEPTED -> this.isEnabled = false
        }
    }
}