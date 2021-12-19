package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentDashboardBinding
import com.buzuriu.dogapp.databinding.FragmentSelectBreedBinding
import com.buzuriu.dogapp.viewModels.SelectBreedViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectBreedFragment : BaseBoundFragment<SelectBreedViewModel, FragmentSelectBreedBinding>(
    SelectBreedViewModel::class.java
){
    private lateinit var currentBinding: FragmentSelectBreedBinding

    override val layoutId: Int
        get() = R.layout.fragment_select_breed


    override fun setupDataBinding(binding: FragmentSelectBreedBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    private fun setupRecyclerView()
    {
        val recyclerView = currentBinding.breedList
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.breedAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

}