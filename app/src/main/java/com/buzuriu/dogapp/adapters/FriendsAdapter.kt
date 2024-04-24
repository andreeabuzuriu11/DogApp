package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FriendCellBinding
import com.buzuriu.dogapp.databinding.FriendRequestSentCellBinding
import com.buzuriu.dogapp.models.UserObj

class FriendsAdapter(var userList: ArrayList<UserObj>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = FriendCellBinding.inflate(layoutInflater, parent, false)
        return FriendsViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = userList[position]
        if (holder is FriendsViewHolder) {
            (holder).bind(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class FriendsViewHolder(var applicationBinding: FriendCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(user: UserObj) {
            applicationBinding.user = user
        }
    }
}