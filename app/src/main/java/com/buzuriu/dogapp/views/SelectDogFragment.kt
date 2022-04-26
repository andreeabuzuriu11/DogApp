package com.buzuriu.dogapp.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectDogBinding
import com.buzuriu.dogapp.viewModels.SelectDogViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectDogFragment : BaseBoundFragment<SelectDogViewModel, FragmentSelectDogBinding>(
    SelectDogViewModel::class.java
) {
    private lateinit var currentBinding: FragmentSelectDogBinding

    override val layoutId: Int
        get() = R.layout.fragment_select_dog

    override fun setupDataBinding(binding: FragmentSelectDogBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.dogList
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.dogNameAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSearchByNameEditText()
        setupRecyclerView()
    }

    private fun initSearchByNameEditText() {
        currentBinding.selectDogEditText.addTextChangedListener(object : TextWatcher {
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