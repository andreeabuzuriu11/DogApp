package com.buzuriu.dogapp.views.main.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentHistoryBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class HistoryFragment :
    BaseBoundFragment<HistoryViewModel, FragmentHistoryBinding>(HistoryViewModel::class.java) {
    override val layoutId: Int = R.layout.fragment_history
    private lateinit var currentBinding : FragmentHistoryBinding

    override fun setupDataBinding(binding: FragmentHistoryBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        try {
            mViewModel.nrOfStars.observe(requireActivity(), Observer {

            })
        } catch (e: java.lang.Exception) {
            Log.d("Error", "Something went wrong: " + e.message)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.reviewNotificationList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.reviewNotificationAdapter
    }
}