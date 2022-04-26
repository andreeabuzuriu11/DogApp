package com.buzuriu.dogapp.views.main.ui.my_meetings

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentMyMeetingsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class MyMeetingsFragment :
    BaseBoundFragment<MyMeetingsViewModel, FragmentMyMeetingsBinding>(MyMeetingsViewModel::class.java) {
    private lateinit var currentBinding: FragmentMyMeetingsBinding

    override val layoutId: Int
        get() = R.layout.fragment_my_meetings

    override fun setupDataBinding(binding: FragmentMyMeetingsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.meetingsList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.meetingAdapter
    }


}