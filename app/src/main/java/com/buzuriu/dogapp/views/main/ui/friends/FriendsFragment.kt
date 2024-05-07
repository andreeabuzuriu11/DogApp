package com.buzuriu.dogapp.views.main.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.FragmentFriendsBinding
import com.buzuriu.dogapp.views.base.BaseBoundFragment
import kotlinx.coroutines.launch


class FriendsFragment :
    BaseBoundFragment<FriendsViewModel, FragmentFriendsBinding>(FriendsViewModel::class.java) {

    private lateinit var currentBinding: FragmentFriendsBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    protected var mView: View? = null

    override fun setupDataBinding(binding: FragmentFriendsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override val layoutId: Int = com.buzuriu.dogapp.R.layout.fragment_friends


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMyFriendsList()
        setupFriendsRequestList()

//        val view = inflater!!.inflate(R.layout.fragment_friends, container, false)

        var swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.listSwipeRefreshContainer)



        swipeRefreshLayout.setOnRefreshListener {

            // on below line we are setting is refreshing to false.
            swipeRefreshLayout.isRefreshing = false

            // on below line we are shuffling our list using random

            // read data again
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.fetchMyFriendAndFriendsRequest()
            }
            // on below line we are notifying adapter

            // that data has changed in recycler view.
            currentBinding.usersFoundList.adapter!!.notifyDataSetChanged()
        }


    }


    private fun setupMyFriendsList() {
        val recyclerView = currentBinding.usersFoundList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.friendsAdapter
    }

    private fun setupFriendsRequestList() {
        val recyclerView = currentBinding.requestsList
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )//LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.friendsRequestAdapter
    }

}