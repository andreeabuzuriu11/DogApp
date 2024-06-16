package com.buzuriu.dogapp.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentEditStateBinding
import com.buzuriu.dogapp.viewModels.EditStateViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class EditStateFragment :
    BaseBoundFragment<EditStateViewModel, FragmentEditStateBinding>(
        EditStateViewModel::class.java
    ) {
    private lateinit var currentBinding: FragmentEditStateBinding

    override fun setupDataBinding(binding: FragmentEditStateBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    override val layoutId: Int
        get() = R.layout.fragment_edit_state

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        initSearchByNameEditText()
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.statesList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        recyclerView.adapter = mViewModel.stateAdapter
    }

    private fun initSearchByNameEditText() {
        currentBinding.selectCountryEditText.addTextChangedListener(object : TextWatcher {
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


