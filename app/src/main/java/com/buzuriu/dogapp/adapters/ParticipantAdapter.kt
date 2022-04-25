package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.ParticipantCellBinding
import com.buzuriu.dogapp.models.ParticipantObj

class ParticipantAdapter(private var participantsList: ArrayList<ParticipantObj>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = ParticipantCellBinding.inflate(
            layoutInflater, parent, false
        )
        return ParticipantViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val participant = participantsList[position]
        if (holder is ParticipantAdapter.ParticipantViewHolder) {
            (holder).bind(participant)
        }
    }

    override fun getItemCount(): Int {
        return participantsList.size
    }

    inner class ParticipantViewHolder(private var applicationBinding: ParticipantCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(participant: ParticipantObj) {
            applicationBinding.participant = participant
        }
    }
}