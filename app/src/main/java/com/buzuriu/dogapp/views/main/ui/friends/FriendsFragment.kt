package com.buzuriu.dogapp.views.main.ui.friends

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentFriendsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class FriendsFragment : BaseBoundFragment<FriendsViewModel, FragmentFriendsBinding>(FriendsViewModel::class.java) {

    private lateinit var currentBinding: FragmentFriendsBinding

    override fun setupDataBinding(binding: FragmentFriendsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override val layoutId: Int = R.layout.fragment_friends

}