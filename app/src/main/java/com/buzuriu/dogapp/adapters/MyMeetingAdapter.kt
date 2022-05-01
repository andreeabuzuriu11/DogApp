package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.AttendedMeetingCellBinding
import com.buzuriu.dogapp.databinding.MeetingSectionBinding
import com.buzuriu.dogapp.databinding.MyMeetingCellBinding
import com.buzuriu.dogapp.models.IMeetingObj
import com.buzuriu.dogapp.models.MeetingSectionObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.services.FirebaseAuthService
import com.buzuriu.dogapp.views.main.ui.my_meetings.MyMeetingsViewModel
import com.google.firebase.auth.FirebaseUser
import kotlin.reflect.KFunction1

class MyMeetingAdapter(
    private var meetingList: ArrayList<IMeetingObj>,
    var mySelectedMeeting: KFunction1<MyCustomMeetingObj, Unit>,
    private val viewModel: MyMeetingsViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val MyMeet = 0
        private const val JoinedMeet = 1
        private const val MeetSubtitle = 2
    }

    private var firebaseAuthService = FirebaseAuthService()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == MyMeet) {
            val applicationBinding =
                MyMeetingCellBinding.inflate(layoutInflater, parent, false)
            MyMeetingViewHolder(applicationBinding)
        } else if (viewType == JoinedMeet) {
            val applicationBinding =
                AttendedMeetingCellBinding.inflate(layoutInflater, parent, false)
            AttendedMeetingViewHolder(applicationBinding)
        } else if (viewType == MeetSubtitle)
        {
            val applicationBinding =
                MeetingSectionBinding.inflate(layoutInflater, parent, false)
            MeetingSectionViewHolder(applicationBinding)

        }
        else throw IllegalArgumentException("Unsupported view type for view holder")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val meet = meetingList[position]

        when (holder) {
            is MyMeetingViewHolder -> holder.bind(meet as MyCustomMeetingObj)
            is AttendedMeetingViewHolder -> holder.bind(meet as MyCustomMeetingObj)
            is MeetingSectionViewHolder -> holder.bind(meet as MeetingSectionObj)
        }
    }

    override fun getItemCount(): Int {
        return meetingList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentUser: FirebaseUser? = firebaseAuthService.getCurrentUser()
        val meet = meetingList[position]

        return if (meet is MyCustomMeetingObj) {
            if (meet.meetingObj!!.userUid == currentUser!!.uid)
                MyMeet
            else
                JoinedMeet
        } else
            MeetSubtitle
    }


    inner class MyMeetingViewHolder(var applicationBinding: MyMeetingCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.myMeetingCell.setOnClickListener {
                mySelectedMeeting(meeting)
            }
        }
    }

    inner class AttendedMeetingViewHolder(var applicationBinding: AttendedMeetingCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.attendedMeetingCell.setOnClickListener {
                mySelectedMeeting(meeting)
            }
            applicationBinding.joinButton.setOnClickListener {
                viewModel.leaveMeeting(meeting)
            }
        }
    }

    inner class MeetingSectionViewHolder(var applicationBinding: MeetingSectionBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meetingSection: MeetingSectionObj) {
            applicationBinding.sectionName = meetingSection.sectionName
        }
    }
}