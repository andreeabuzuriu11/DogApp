package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityMainBinding
import com.buzuriu.dogapp.viewModels.MainViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity


class MainActivity : BaseBoundActivity<MainViewModel, ActivityMainBinding>(MainViewModel::class.java) {

    override val layoutId: Int= R.layout.activity_main

    override fun setupDataBinding(binding: ActivityMainBinding) {
        binding.viewModel = mViewModel
    }
}