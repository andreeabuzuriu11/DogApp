package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentReviewParticipantsBinding
import com.buzuriu.dogapp.viewModels.ReviewParticipantsViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class ReviewParticipantsFragment :
    BaseBoundFragment<ReviewParticipantsViewModel, FragmentReviewParticipantsBinding>(
        ReviewParticipantsViewModel::class.java
    ) {
    private lateinit var currentBinding: FragmentReviewParticipantsBinding

    override val layoutId: Int
        get() = R.layout.fragment_review_participants

    override fun setupDataBinding(binding: FragmentReviewParticipantsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val participansList = currentBinding.reviewParticipantList
        participansList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        participansList.adapter = mViewModel.participantsAdapter
    }

}