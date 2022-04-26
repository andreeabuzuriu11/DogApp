package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentSelectDogForMeetingJoinBinding
import com.buzuriu.dogapp.viewModels.SelectDogForJoinMeetViewModel
import com.buzuriu.dogapp.views.base.BaseBoundFragment

class SelectDogForJoinMeetFragment : BaseBoundFragment<SelectDogForJoinMeetViewModel, FragmentSelectDogForMeetingJoinBinding>(
    SelectDogForJoinMeetViewModel::class.java)
{
    private lateinit var currentBinding: FragmentSelectDogForMeetingJoinBinding

    override val layoutId: Int
        get() = R.layout.fragment_select_dog_for_meeting_join

    override fun setupDataBinding(binding: FragmentSelectDogForMeetingJoinBinding) {
        currentBinding = binding
        binding.viewModel = mViewModel
    }

    private fun setupRecyclerView()
    {
        val recyclerView = currentBinding.dogList
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.dogJoinMeetAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

}