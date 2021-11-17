package com.buzuriu.dogapp.views.main.ui.home

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentHomeBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment


class HomeFragment : BaseBoundFragment<HomeViewModel, FragmentHomeBinding>(HomeViewModel::class.java) {
    override val layoutId: Int = R.layout.fragment_home

    override fun setupDataBinding(binding: FragmentHomeBinding) {

    }

}