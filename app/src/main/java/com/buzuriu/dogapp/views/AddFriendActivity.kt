package com.buzuriu.dogapp.views

import android.os.Build
import android.os.Bundle
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityAddFriendBinding
import com.buzuriu.dogapp.viewModels.AddFriendViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AddFriendActivity :
    BaseBoundActivity<AddFriendViewModel, ActivityAddFriendBinding>(AddFriendViewModel::class.java) {

    override val layoutId = R.layout.activity_add_friend
    private var activityAddFriendBinding: ActivityAddFriendBinding? = null
    private lateinit var searchView: SearchView


    override fun setupDataBinding(binding: ActivityAddFriendBinding) {
        binding.viewModel = mViewModel
        activityAddFriendBinding = binding
        activityAddFriendBinding?.viewModel = mViewModel
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        val recyclerView = activityAddFriendBinding?.usersFoundList
        recyclerView?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView?.adapter = mViewModel.userAdapter

        val currentSearchView =
            findViewById<SearchView>(com.buzuriu.dogapp.R.id.search_view)

        if (currentSearchView != null) {
            searchView = currentSearchView
        }

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextChange(newText: String): Boolean {
                    val vm = mViewModel

                    vm.findUser(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    val vm = mViewModel
                    vm.findUser(query)

                    //todo fix loading
                    vm.showLoading(false)

                    return false
                }
            })
    }


}