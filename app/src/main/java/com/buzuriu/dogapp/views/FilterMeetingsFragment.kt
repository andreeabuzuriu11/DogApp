package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentFilterMeetingsBinding
import com.buzuriu.dogapp.viewModels.FilterMeetingsViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class FilterMeetingsFragment :
    BaseBoundFragment<FilterMeetingsViewModel, FragmentFilterMeetingsBinding>(
        FilterMeetingsViewModel::class.java
    ) {
    private lateinit var currentBinding: FragmentFilterMeetingsBinding

    override val layoutId: Int
        get() = R.layout.fragment_filter_meetings

    override fun setupDataBinding(binding: FragmentFilterMeetingsBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.filterMeetingsByTime
        recyclerView.layoutManager =
            GridLayoutManager(activity, 3)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.filterAdapterTime

        val recyclerView2 = currentBinding.filterDogsByGender
        recyclerView2.layoutManager =
            GridLayoutManager(activity, 3)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView2.adapter = mViewModel.filterAdapterDogGender

        val recyclerView3 = currentBinding.filterDogsByBreed
        recyclerView3.layoutManager =
            GridLayoutManager(activity, 3)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView3.adapter = mViewModel.filterAdapterDogBreed

        val recyclerView4 = currentBinding.filterUsersByGender
        recyclerView4.layoutManager =
            GridLayoutManager(activity, 3)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView4.adapter = mViewModel.filterAdapterUserGender
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

}