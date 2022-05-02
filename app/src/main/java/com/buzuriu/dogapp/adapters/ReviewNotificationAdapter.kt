package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.ReviewCellBinding
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import kotlin.reflect.KFunction1

class ReviewNotificationAdapter(
    private var reviewNotificationList: ArrayList<MyCustomMeetingObj>,
    var selectedPastMeeting: KFunction1<MyCustomMeetingObj, Unit>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = ReviewCellBinding.inflate(
            layoutInflater, parent, false
        )
        return ReviewNotificationViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reviewNotif = reviewNotificationList[position]
        if (holder is ReviewNotificationAdapter.ReviewNotificationViewHolder) {
            (holder).bind(reviewNotif)
        }
    }

    override fun getItemCount(): Int {
        return reviewNotificationList.size
    }

    inner class ReviewNotificationViewHolder(private var applicationBinding: ReviewCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.meetingCell.setOnClickListener {
                selectedPastMeeting(meeting)
            }
        }
    }
}
