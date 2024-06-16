package com.buzuriu.dogapp.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentEditCityBinding
import com.buzuriu.dogapp.viewModels.EditCityViewModel

import com.buzuriu.dogapp.views.base.BaseBoundFragment

class EditCityFragment : BaseBoundFragment<EditCityViewModel, FragmentEditCityBinding>(
    EditCityViewModel::class.java
) {
    private lateinit var currentBinding: FragmentEditCityBinding

    override fun setupDataBinding(binding: FragmentEditCityBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_city

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initSearchByNameEditText()
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

    private fun initSearchByNameEditText() {
        currentBinding.selectCityEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mViewModel.searchByName(charSequence.toString())
            }

        })
    }
}

