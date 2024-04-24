package com.buzuriu.dogapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FriendRequestSentCellBinding
import com.buzuriu.dogapp.models.UserObj
import kotlin.reflect.KFunction1

class UserAdapter(var userList: ArrayList<UserObj>, var sendRequest: KFunction1<UserObj, Unit>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val applicationBinding = FriendRequestSentCellBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(applicationBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = userList[position]
        if (holder is UserViewHolder) {
            (holder).bind(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(var applicationBinding: FriendRequestSentCellBinding) :
        RecyclerView.ViewHolder(applicationBinding.root) {
        fun bind(user: UserObj) {
            applicationBinding.user = user
            applicationBinding.sendReq.setOnClickListener {
                sendRequest(user)
            }
        }
    }
}