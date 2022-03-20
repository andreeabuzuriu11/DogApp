package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityEditAccountBinding
import com.buzuriu.dogapp.viewModels.EditAccountViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class EditAccountActivity : BaseBoundActivity<EditAccountViewModel, ActivityEditAccountBinding>(
    EditAccountViewModel::class.java
) {
    override val layoutId: Int
        get() = R.layout.activity_edit_account

    override fun setupDataBinding(binding: ActivityEditAccountBinding) {
        binding.viewModel = mViewModel
    }
}