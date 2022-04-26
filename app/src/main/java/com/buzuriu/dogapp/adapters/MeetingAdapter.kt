package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.MeetingCellBinding
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.views.main.ui.map.MapViewModel
import kotlin.reflect.KFunction1

class MeetingAdapter(
    private var meetingList: ArrayList<MyCustomMeetingObj>,
    var selectedMeeting: KFunction1<MyCustomMeetingObj, Unit>,
    private val viewModel: MapViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            MeetingCellBinding.inflate(layoutInflater, parent, false)
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

    inner class MeetingViewHolder(private var applicationBinding: MeetingCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.meetingCell.setOnClickListener {
                selectedMeeting(meeting)
            }
            applicationBinding.joinButton.setOnClickListener {
                viewModel.joinMeeting(meeting)
            }
            applicationBinding.meetingState = meeting.meetingStateEnum
        }
    }
}