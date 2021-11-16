package com.buzuriu.dogapp.views.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityLoginBinding
import com.buzuriu.dogapp.viewModels.auth.LoginViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class LoginActivity : BaseBoundActivity<LoginViewModel, ActivityLoginBinding>(LoginViewModel::class.java) {

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun setupDataBinding(binding: ActivityLoginBinding) {
        binding.viewModel = mViewModel
    }
}