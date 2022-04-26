package com.buzuriu.dogapp.views.main.ui.my_dogs

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentMyDogsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment


class MyDogsFragment : BaseBoundFragment<MyDogsViewModel, FragmentMyDogsBinding>(MyDogsViewModel::class.java) {

    private lateinit var currentBinding: FragmentMyDogsBinding
    override val layoutId: Int= R.layout.fragment_my_dogs

    override fun setupDataBinding(binding: FragmentMyDogsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setHasOptionsMenu(true)
    }
    private fun setupRecyclerView()
    {
        val recyclerView = currentBinding.dogsList
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.dogAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_logout) {
            mViewModel.logout()
        }
        if (id == R.id.action_account) {
            mViewModel.goToAccountDetails()
        }
        return super.onOptionsItemSelected(item)
    }

}