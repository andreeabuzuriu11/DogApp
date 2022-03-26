package com.buzuriu.dogapp.views

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityEditMeetingBinding
import com.buzuriu.dogapp.viewModels.EditMeetingViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class EditMeetingActivity : BaseBoundActivity<EditMeetingViewModel,ActivityEditMeetingBinding>(EditMeetingViewModel::class.java) {
    override val layoutId: Int
        get() = R.layout.activity_edit_meeting

    override fun setupDataBinding(binding: ActivityEditMeetingBinding) {
        binding.viewModel = mViewModel
    }

}