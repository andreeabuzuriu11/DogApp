package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectCityBinding
import com.buzuriu.dogapp.viewModels.SelectCityViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectCityFragment : BaseBoundFragment<SelectCityViewModel, FragmentSelectCityBinding>(
    SelectCityViewModel::class.java
) {
    private lateinit var currentBinding: FragmentSelectCityBinding

    override fun setupDataBinding(binding: FragmentSelectCityBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    override val layoutId: Int
        get() = R.layout.fragment_select_city
}