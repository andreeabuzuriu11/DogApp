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

    override val layoutId: Int = R.layout.fragment_friends


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMyFriendsList()
        setupFriendsRequestList()

        var swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.listSwipeRefreshContainer)

        swipeRefreshLayout.setOnRefreshListener {
            // set the is refreshing to false so the loading view stops
            swipeRefreshLayout.isRefreshing = false

            // read data again
            viewLifecycleOwner.lifecycleScope.launch {
                mViewModel.fetchMyFriendAndFriendsRequest()
            }

            // notify the adapter
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