package com.buzuriu.dogapp.views.main.ui.notifications

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentNotificationsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class NotificationsFragment :
    BaseBoundFragment<NotificationsViewModel, FragmentNotificationsBinding>(NotificationsViewModel::class.java) {
    override val layoutId: Int = R.layout.fragment_notifications
    private lateinit var currentBinding : FragmentNotificationsBinding

    override fun setupDataBinding(binding: FragmentNotificationsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.reviewNotificationList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.reviewNotifAdapter
    }
}