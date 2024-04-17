package com.buzuriu.dogapp.views.main.ui.friends

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.databinding.FragmentFriendsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment


class FriendsFragment :
    BaseBoundFragment<FriendsViewModel, FragmentFriendsBinding>(FriendsViewModel::class.java) {

    private lateinit var currentBinding: FragmentFriendsBinding
    private lateinit var searchView: SearchView

    override fun setupDataBinding(binding: FragmentFriendsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override val layoutId: Int = com.buzuriu.dogapp.R.layout.fragment_friends


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()


        val currentSearchView =
            getView()?.findViewById<SearchView>(com.buzuriu.dogapp.R.id.search_by_email)

        if (currentSearchView != null) {
            searchView = currentSearchView
        }

        searchView.setOnQueryTextListener(object :  SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val vm = mViewModel
                val foundUser = vm.findUser(query)

                if (foundUser!=null)
                {
                    return true
                }
                return false
            }
        })
    }

    private fun setupRecyclerView() {
        val recyclerView = currentBinding.usersFoundList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.userAdapter
    }
}