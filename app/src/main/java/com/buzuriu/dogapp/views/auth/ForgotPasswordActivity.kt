package com.buzuriu.dogapp.views.auth

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityForgotPasswordBinding
import com.buzuriu.dogapp.viewModels.auth.ForgotPasswordViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class ForgotPasswordActivity :
    BaseBoundActivity<ForgotPasswordViewModel, ActivityForgotPasswordBinding>(
        ForgotPasswordViewModel::class.java
    ) {
    override val layoutId: Int
        get() = R.layout.activity_forgot_password

    override fun setupDataBinding(binding: ActivityForgotPasswordBinding) {
        binding.viewModel = mViewModel
    }

}