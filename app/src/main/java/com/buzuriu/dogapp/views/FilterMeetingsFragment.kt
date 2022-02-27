package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentFilterMeetingsBinding
import com.buzuriu.dogapp.viewModels.FilterMeetingsViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class FilterMeetingsFragment : BaseBoundFragment<FilterMeetingsViewModel, FragmentFilterMeetingsBinding>(
    FilterMeetingsViewModel::class.java
){
    private lateinit var currentBinding: FragmentFilterMeetingsBinding

    override val layoutId: Int
        get() = R.layout.fragment_filter_meetings

    override fun setupDataBinding(binding: FragmentFilterMeetingsBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }
}