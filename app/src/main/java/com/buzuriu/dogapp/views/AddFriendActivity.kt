package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityAddFriendBinding
import com.buzuriu.dogapp.viewModels.AddFriendViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AddFriendActivity :
    BaseBoundActivity<AddFriendViewModel, ActivityAddFriendBinding>(AddFriendViewModel::class.java) {

    override val layoutId = R.layout.activity_add_friend

    override fun setupDataBinding(binding: ActivityAddFriendBinding) {
        binding.viewModel = mViewModel
    }
}