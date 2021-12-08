package com.buzuriu.dogapp.views.main.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentDashboardBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class DashboardFragment : BaseBoundFragment<DashboardViewModel, FragmentDashboardBinding>(DashboardViewModel::class.java) {

    private lateinit var currentBinding: FragmentDashboardBinding
    override val layoutId: Int= R.layout.fragment_dashboard

    override fun setupDataBinding(binding: FragmentDashboardBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }
    private fun setupRecyclerView()
    {
        val recyclerView = currentBinding.dogsList
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.dogAdapter
    }

}