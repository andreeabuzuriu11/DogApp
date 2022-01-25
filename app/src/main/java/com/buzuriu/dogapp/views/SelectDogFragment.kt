package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectDogBinding
import com.buzuriu.dogapp.viewModels.SelectDogViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectDogFragment : BaseBoundFragment<SelectDogViewModel, FragmentSelectDogBinding>(
SelectDogViewModel::class.java)
{
    private lateinit var currentBinding: FragmentSelectDogBinding

    override val layoutId: Int
        get() = R.layout.fragment_select_dog

    override fun setupDataBinding(binding: FragmentSelectDogBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

}