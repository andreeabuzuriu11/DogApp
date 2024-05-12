package com.buzuriu.dogapp.views.main.ui.friends

import android.os.Bundle
import android.view.View
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

    override fun setupDataBinding(binding: FragmentFriendsBinding) {
        binding.viewModel = mViewModel
        currentBinding = binding
    }

    override val layoutId: Int = R.layout.fragment_friends


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMyFriendsList()
        setupFriendsRequestList()

        setupRefreshMyFriendsList(view)
        setupRefreshFriendsRequest(view)

    }

    private fun setupRefreshMyFriendsList(view: View) {
        var myFriendsRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.myFriendsRefreshLayout)

        myFriendsRefreshLayout.setOnRefreshListener {
            // set the is refreshing to false so the loading view stops
            myFriendsRefreshLayout.isRefreshing = false

            // read data again
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.fetchMyFriends()
            }

            // notify the adapter
            currentBinding.usersFoundList.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setupRefreshFriendsRequest(view: View) {
        var friendsRequestRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.friendsRequestRefreshLayout)

        friendsRequestRefreshLayout.setOnRefreshListener {
            // set the is refreshing to false so the loading view stops
            friendsRequestRefreshLayout.isRefreshing = false

            // read data again
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.fetchFriendsRequest()
            }

            // notify the adapter
            currentBinding.requestsList.adapter!!.notifyDataSetChanged()
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