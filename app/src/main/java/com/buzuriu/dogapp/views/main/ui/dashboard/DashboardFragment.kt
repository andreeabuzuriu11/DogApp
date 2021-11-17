package com.buzuriu.dogapp.views.main.ui.dashboard

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentDashboardBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class DashboardFragment : BaseBoundFragment<DashboardViewModel, FragmentDashboardBinding>(DashboardViewModel::class.java) {

    override val layoutId: Int= R.layout.fragment_dashboard

    override fun setupDataBinding(binding: FragmentDashboardBinding) {

    }

}