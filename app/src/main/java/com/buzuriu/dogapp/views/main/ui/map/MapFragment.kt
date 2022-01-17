package com.buzuriu.dogapp.views.main.ui.map

import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentMapBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

// NotificationsFragment : BaseBoundFragment<NotificationsViewModel,FragmentNotificationsBinding>(NotificationsViewModel::class.java) {

class MapFragment : BaseBoundFragment<MapViewModel, FragmentMapBinding>(MapViewModel::class.java) {
    override val layoutId: Int = R.layout.fragment_map

    override fun setupDataBinding(binding: FragmentMapBinding) {

    }

}