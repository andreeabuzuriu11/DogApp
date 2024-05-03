package com.buzuriu.dogapp.views

import android.os.Bundle
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

class FriendProfileActivity : BaseBoundActivity<FriendProfileViewModel, ActivityFriendProfileBinding>(
    FriendProfileViewModel::class.java) {

    override val layoutId= R.layout.activity_friend_profile

    override fun setupDataBinding(binding: ActivityFriendProfileBinding) {
        binding.viewModel = mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setupRecyclerView(binding: ActivityFriendProfileBinding) {

        binding.friendEvents.layoutManager  = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.friendEvents.adapter = mViewModel.meetingsAdapter
    }

}