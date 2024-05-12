package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
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
        val recyclerViewTime = currentBinding.filterMeetingsByTime
        recyclerViewTime.layoutManager =
            GridLayoutManager(activity, 3)
        recyclerViewTime.adapter = mViewModel.filterAdapterTime

        val recyclerViewDogGender = currentBinding.filterDogsByGender
        recyclerViewDogGender.layoutManager =
            GridLayoutManager(activity, 3)
        recyclerViewDogGender.adapter = mViewModel.filterAdapterDogGender

        val recyclerViewDogBreed = currentBinding.filterDogsByBreed
        recyclerViewDogBreed.layoutManager =
            GridLayoutManager(activity, 3)
        recyclerViewDogBreed.adapter = mViewModel.filterAdapterDogBreed

        val recyclerViewUserGender = currentBinding.filterUsersByGender
        recyclerViewUserGender.layoutManager =
            GridLayoutManager(activity, 3)
        recyclerViewUserGender.adapter = mViewModel.filterAdapterUserGender
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        val languages = mViewModel.getTemperamentList()

        val spinner = view.findViewById<Spinner>(R.id.temperamentSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_item, languages
            )
            spinner.adapter = adapter
        }

    }
}