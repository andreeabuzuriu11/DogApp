package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FriendsMeetingCellBinding
import com.buzuriu.dogapp.databinding.MeetingCellBinding
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.viewModels.FriendProfileViewModel
import kotlin.reflect.KFunction1

class FriendMeetingAdapter(
    private var meetingList: ArrayList<MyCustomMeetingObj>,
    var selectedMeeting: KFunction1<MyCustomMeetingObj, Unit>,
    private val viewModel: FriendProfileViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            FriendsMeetingCellBinding.inflate(layoutInflater, parent, false)
        return MeetingViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val meeting = meetingList[position]
        if (holder is MeetingAdapter.MeetingViewHolder) {
            (holder).bind(meeting)
        }
    }

    override fun getItemCount(): Int {
        return meetingList.size
    }

    inner class MeetingViewHolder(private var applicationBinding: FriendsMeetingCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.meetingCell.setOnClickListener {
                selectedMeeting(meeting)
            }
            // TODO fix join logic
            //applicationBinding.joinButton.setOnClickListener {
            //    viewModel.joinOrLeaveMeeting(meeting)
            //}
            applicationBinding.meetingState = meeting.meetingStateEnum
        }
    }
}