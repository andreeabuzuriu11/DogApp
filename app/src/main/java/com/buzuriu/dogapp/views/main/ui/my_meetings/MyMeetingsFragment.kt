package com.buzuriu.dogapp.views.main.ui.my_meetings

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentMyMeetingsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class MyMeetingsFragment : BaseBoundFragment<MyMeetingsViewModel, FragmentMyMeetingsBinding> (MyMeetingsViewModel::class.java)
{
    override val layoutId: Int
        get() = R.layout.fragment_my_meetings

    override fun setupDataBinding(binding: FragmentMyMeetingsBinding) {
        binding.viewModel = mViewModel
    }


}