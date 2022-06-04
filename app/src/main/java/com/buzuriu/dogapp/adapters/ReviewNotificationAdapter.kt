package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.*
import com.buzuriu.dogapp.models.IMeetingObj
import com.buzuriu.dogapp.models.MeetingSectionObj
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import com.buzuriu.dogapp.services.FirebaseAuthService
import com.buzuriu.dogapp.views.main.ui.notifications.HistoryViewModel
import com.google.firebase.auth.FirebaseUser
import kotlin.reflect.KFunction1

class ReviewNotificationAdapter(
    private var reviewNotificationList: ArrayList<IMeetingObj>,
    var selectedPastMeeting: KFunction1<MyCustomMeetingObj, Unit>,
    var viewModel: HistoryViewModel
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
                ReviewMyMeetCellBinding.inflate(layoutInflater, parent, false)
            ReviewNotificationCreatedMeetViewHolder(applicationBinding)
        } else if (viewType == JoinedMeet) {
            val applicationBinding =
                ReviewCellBinding.inflate(layoutInflater, parent, false)
            ReviewNotificationJoinedMeetViewHolder(applicationBinding)
        } else if (viewType == MeetSubtitle)
        {
            val applicationBinding =
                MeetingSectionBinding.inflate(layoutInflater, parent, false)
            MeetingSectionViewHolder(applicationBinding)

        }
        else throw IllegalArgumentException("Unsupported view type for view holder")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reviewNotif = reviewNotificationList[position]

        when (holder) {
            is ReviewNotificationCreatedMeetViewHolder -> holder.bind(reviewNotif as MyCustomMeetingObj)
            is ReviewNotificationJoinedMeetViewHolder -> holder.bind(reviewNotif as MyCustomMeetingObj)
            is MeetingSectionViewHolder -> holder.bind(reviewNotif as MeetingSectionObj)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentUser: FirebaseUser? = firebaseAuthService.getCurrentUser()
        val meet = reviewNotificationList[position]

        return if (meet is MyCustomMeetingObj) {
            if (meet.meetingObj!!.userUid == currentUser!!.uid)
                MyMeet
            else
                JoinedMeet
        } else
            MeetSubtitle
    }

    override fun getItemCount(): Int {
        return reviewNotificationList.size
    }

    inner class ReviewNotificationJoinedMeetViewHolder(private var applicationBinding: ReviewCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.meetingCell.setOnClickListener {
                selectedPastMeeting(meeting)
            }
            applicationBinding.reviewParticipantsButton.setOnClickListener{
                viewModel.openReviewParticipantsFragment(meeting)
            }
        }
    }

    inner class ReviewNotificationCreatedMeetViewHolder(private var applicationBinding: ReviewMyMeetCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting: MyCustomMeetingObj) {
            applicationBinding.meeting = meeting
            applicationBinding.meetingCell.setOnClickListener {
                selectedPastMeeting(meeting)
            }
            applicationBinding.reviewParticipantsButton.setOnClickListener{
                viewModel.openReviewParticipantsFragment(meeting)
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
