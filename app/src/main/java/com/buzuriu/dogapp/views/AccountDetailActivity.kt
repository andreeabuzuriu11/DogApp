package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityAccountDetailBinding
import com.buzuriu.dogapp.viewModels.AccountDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AccountDetailActivity : BaseBoundActivity<AccountDetailViewModel, ActivityAccountDetailBinding>(
    AccountDetailViewModel::class.java){
    override val layoutId: Int
        get() = R.layout.activity_account_detail

    override fun setupDataBinding(binding: ActivityAccountDetailBinding) {
        binding.viewModel = mViewModel
    }
}