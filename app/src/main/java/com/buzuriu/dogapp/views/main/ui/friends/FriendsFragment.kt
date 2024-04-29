package com.buzuriu.dogapp.views.main.ui.friends

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FragmentFriendsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment


class FriendsFragment :
    BaseBoundFragment<FriendsViewModel, FragmentFriendsBinding>(FriendsViewModel::class.java) {

    private lateinit var currentBinding: FragmentFriendsBinding

    override fun setupDataBinding(binding: FragmentFriendsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override val layoutId: Int = com.buzuriu.dogapp.R.layout.fragment_friends


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMyFriendsList()
        setupFriendsRequestList()
    }

    private fun setupMyFriendsList() {
        val recyclerView = currentBinding.usersFoundList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.friendsAdapter
    }

    private fun setupFriendsRequestList() {
        val recyclerView = currentBinding.requestsList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.friendsRequestAdapter
    }

}