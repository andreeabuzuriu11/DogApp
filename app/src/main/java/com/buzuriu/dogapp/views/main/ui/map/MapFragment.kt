package com.buzuriu.dogapp.views.main.ui.map

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        setupRecyclerViewForMeetings()
        setupRecyclerViewForSelectedFilters()
        setHasOptionsMenu(true)
    }

    private fun setupRecyclerViewForMeetings()
    {
        val recyclerView = currentBinding.meetingsList
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.meetingAdapter
    }

    private fun setupRecyclerViewForSelectedFilters()
    {
        val recyclerView = currentBinding.selectedFiltersList
        recyclerView.layoutManager = GridLayoutManager(activity,3) /*LinearLayoutManager(activity, RecyclerView.VERTICAL, false)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)*/
        recyclerView.adapter = mViewModel.filterAdapter
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
            R.id.filter_list -> {
                mViewModel.showFilters()
                Log.d("MapFragment", "Filter list")
            }
            R.id.search -> Log.d("MapFragment", "Search in list")
        }

        return super.onOptionsItemSelected(item)
    }

}