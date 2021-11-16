package com.buzuriu.dogapp.views.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityRegisterBinding
import com.buzuriu.dogapp.viewModels.auth.RegisterViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class RegisterActivity : BaseBoundActivity<RegisterViewModel, ActivityRegisterBinding>(RegisterViewModel::class.java) {
    override val layoutId: Int
        get() = R.layout.activity_register

    override fun setupDataBinding(binding: ActivityRegisterBinding) {
        binding.viewModel = mViewModel
    }
}
