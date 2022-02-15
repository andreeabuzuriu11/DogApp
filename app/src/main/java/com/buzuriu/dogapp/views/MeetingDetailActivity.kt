package com.buzuriu.dogapp.views

import android.os.Bundle
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityMeetingDetailBinding
import com.buzuriu.dogapp.viewModels.MeetingDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class MeetingDetailActivity : BaseBoundActivity<MeetingDetailViewModel, ActivityMeetingDetailBinding>(
    MeetingDetailViewModel::class.java) {

    override val layoutId = R.layout.activity_meeting_detail
    override fun setupDataBinding(binding: ActivityMeetingDetailBinding) {
        binding.viewModel =  mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}