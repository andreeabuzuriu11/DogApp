package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectBreedBinding
import com.buzuriu.dogapp.viewModels.SelectBreedViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectBreedFragment : BaseBoundFragment<SelectBreedViewModel, FragmentSelectBreedBinding>(
    SelectBreedViewModel::class.java
){

    override val layoutId: Int
        get() = R.layout.fragment_select_breed


    override fun setupDataBinding(binding: FragmentSelectBreedBinding) {
        binding.viewModel = mViewModel
    }


}