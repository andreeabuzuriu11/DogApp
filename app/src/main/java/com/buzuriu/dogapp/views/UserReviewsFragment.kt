package com.buzuriu.dogapp.views

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

    override val layoutId: Int
        get() = R.layout.fragment_user_reviews

}