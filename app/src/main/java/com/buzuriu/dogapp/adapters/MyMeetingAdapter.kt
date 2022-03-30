package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.MyMeetingCellBinding
import com.buzuriu.dogapp.models.MyCustomMeetingObj
import kotlin.reflect.KFunction1

class MyMeetingAdapter(var myMeetingList: ArrayList<MyCustomMeetingObj>, var mySelectedMeeting: KFunction1<MyCustomMeetingObj, Unit>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding =
            MyMeetingCellBinding.inflate(layoutInflater, parent, false)
        return MeetingViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val meeting = myMeetingList[position]
        if (holder is MyMeetingAdapter.MeetingViewHolder) {
            (holder).bind(meeting)
        }
    }

    override fun getItemCount(): Int {
        return myMeetingList.size
    }

    inner class MeetingViewHolder(var applicationBinding: MyMeetingCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(meeting : MyCustomMeetingObj)
        {
            applicationBinding.meeting = meeting
            applicationBinding.myMeetingCell.setOnClickListener {
                mySelectedMeeting(meeting)
            }
        }
    }
}