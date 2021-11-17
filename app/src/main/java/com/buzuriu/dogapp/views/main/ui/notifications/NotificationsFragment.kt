package com.buzuriu.dogapp.views.main.ui.notifications

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentNotificationsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class NotificationsFragment : BaseBoundFragment<NotificationsViewModel,FragmentNotificationsBinding>(NotificationsViewModel::class.java) {
    override val layoutId: Int = R.layout.fragment_notifications

    override fun setupDataBinding(binding: FragmentNotificationsBinding) {
    }

}