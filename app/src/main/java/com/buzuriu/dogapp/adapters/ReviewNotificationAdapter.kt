package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.ReviewCellBinding
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.views.main.ui.notifications.NotificationsViewModel

class ReviewNotificationAdapter(
    private var reviewNotificationList: ArrayList<MyCustomMeetingObj>,
    private val viewModel: NotificationsViewModel
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
            applicationBinding.saveReview.setOnClickListener {
                viewModel.saveReview(meeting)
            }
        }
    }
}
