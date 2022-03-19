package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityAddDogBinding
import com.buzuriu.dogapp.viewModels.AddDogViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AddDogActivity : BaseBoundActivity<AddDogViewModel, ActivityAddDogBinding>(AddDogViewModel::class.java) {

    override val layoutId: Int
        get() = R.layout.activity_add_dog

    override fun setupDataBinding(binding: ActivityAddDogBinding) {
        binding.viewModel = mViewModel
    }

}