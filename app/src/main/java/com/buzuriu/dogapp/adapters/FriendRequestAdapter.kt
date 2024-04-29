package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FriendRequestAcceptDeclineCellBinding
import com.buzuriu.dogapp.models.UserObj

class FriendRequestAdapter(var friendsReqList: ArrayList<UserObj>, var showFriendProfile: (UserObj) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = FriendRequestAcceptDeclineCellBinding.inflate(layoutInflater, parent, false)
        return FriendRequestViewHolder(applicationBinding)
    }

    override fun getItemCount(): Int {
        return friendsReqList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = friendsReqList[position]
        if (holder is FriendRequestViewHolder) {
            (holder).bind(user)
        }
    }

    inner class FriendRequestViewHolder(var applicationBinding : FriendRequestAcceptDeclineCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(user: UserObj) {
            applicationBinding.user = user
            applicationBinding.friendRequestCell.setOnClickListener {
                println("Friend Req cell pressed")
            }
        }
    }
}