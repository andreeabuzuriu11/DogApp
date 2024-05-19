package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityFriendProfileBinding
import com.buzuriu.dogapp.databinding.ActivityMeetingDetailBinding
import com.buzuriu.dogapp.viewModels.FriendProfileViewModel
import com.buzuriu.dogapp.viewModels.MeetingDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class FriendProfileActivity :
    BaseBoundActivity<FriendProfileViewModel, ActivityFriendProfileBinding>(
        FriendProfileViewModel::class.java
    ) {

    override val layoutId = R.layout.activity_friend_profile

    var activityAddFriendBinding: ActivityFriendProfileBinding? = null
    override fun setupDataBinding(binding: ActivityFriendProfileBinding) {
        binding.viewModel = mViewModel
        activityAddFriendBinding = binding
        activityAddFriendBinding?.viewModel = mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRecyclerView()
        setupDogsRecyclerView()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete, menu)
        return true
    }

    private fun setupRecyclerView() {
        val recyclerView = activityAddFriendBinding!!.meetingsList
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.meetingsAdapter
    }

    private fun setupDogsRecyclerView() {
        val recyclerView = activityAddFriendBinding!!.dogsList
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = mViewModel.dogAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id: Int = item.itemId
        if (id == R.id.delete) {
            mViewModel.deleteFriend()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}