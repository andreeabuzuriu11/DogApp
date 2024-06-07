package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentUserReviewsBinding
import com.buzuriu.dogapp.viewModels.UserReviewsViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class UserReviewsFragment :
    BaseBoundFragment<UserReviewsViewModel, FragmentUserReviewsBinding>(
        UserReviewsViewModel::class.java
    ) {
    private lateinit var currentBinding: FragmentUserReviewsBinding

    override fun setupDataBinding(binding: FragmentUserReviewsBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupReviewsList()
    }

    private fun setupReviewsList() {
        val recyclerView = currentBinding.reviewList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.ratingWithTextAdapter
    }

    override val layoutId: Int
        get() = R.layout.fragment_user_reviews

}