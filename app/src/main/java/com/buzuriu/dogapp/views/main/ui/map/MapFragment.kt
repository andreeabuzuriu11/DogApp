package com.buzuriu.dogapp.views.main.ui.map

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentDashboardBinding
import com.buzuriu.dogapp.databinding.FragmentMapBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class MapFragment : BaseBoundFragment<MapViewModel, FragmentMapBinding>(MapViewModel::class.java) {
    private lateinit var currentBinding: FragmentMapBinding
    override val layoutId: Int= R.layout.fragment_map

    override fun setupDataBinding(binding: FragmentMapBinding) {
        binding.viewModel = mViewModel
        this.currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_meetings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.show_on_map -> {
                mViewModel.showMap()
                Log.d("MapFragment", "Show on map")}
            R.id.filter_list -> Log.d("MapFragment", "Filter list")
            R.id.search -> Log.d("MapFragment", "Search in list")
        }

        return super.onOptionsItemSelected(item)
    }

}