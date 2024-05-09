package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectCountryBinding
import com.buzuriu.dogapp.viewModels.SelectCountryViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectCityFragment : BaseBoundFragment<SelectCountryViewModel, FragmentSelectCountryBinding>(
    SelectCountryViewModel::class.java
) {
    private lateinit var currentBinding: FragmentSelectCountryBinding

    override fun setupDataBinding(binding: FragmentSelectCountryBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    override val layoutId: Int
        get() = R.layout.fragment_select_country

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
//        initSearchByNameEditText()
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.cityList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        recyclerView.adapter = mViewModel.countryAdapter
    }


}