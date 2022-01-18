package com.buzuriu.dogapp.views

import android.os.Bundle
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityAddMeetingBinding
import com.buzuriu.dogapp.viewModels.AddMeetingViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AddMeetingActivity : BaseBoundActivity<AddMeetingViewModel, ActivityAddMeetingBinding>(AddMeetingViewModel::class.java) {
    override val layoutId = R.layout.activity_add_meeting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meeting)
    }

    override fun setupDataBinding(binding: ActivityAddMeetingBinding) {
        binding.viewModel =  mViewModel
    }
}