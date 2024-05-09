package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectCityBinding
import com.buzuriu.dogapp.viewModels.SelectCityViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectCityFragment : BaseBoundFragment<SelectCityViewModel, FragmentSelectCityBinding>(
    SelectCityViewModel::class.java
) {
    private lateinit var currentBinding: FragmentSelectCityBinding

    override fun setupDataBinding(binding: FragmentSelectCityBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    override val layoutId: Int
        get() = R.layout.fragment_select_city

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
        recyclerView.adapter = mViewModel.cityAdapter
    }


}