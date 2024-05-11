package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityDogDetailBinding
import com.buzuriu.dogapp.viewModels.DogDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class DogDetailActivity : BaseBoundActivity<DogDetailViewModel, ActivityDogDetailBinding>(
    DogDetailViewModel::class.java
) {
    override val layoutId = R.layout.activity_dog_detail
    var activityDogDetailBinding: ActivityDogDetailBinding? = null
    override fun setupDataBinding(binding: ActivityDogDetailBinding) {
        binding.viewModel = mViewModel
        activityDogDetailBinding = binding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRecyclerView()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit_and_delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id: Int = item.itemId
        if (id == R.id.edit) {
            mViewModel.editDog()
            return true
        }
        if (id == R.id.delete) {
            mViewModel.deleteDog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        val recyclerView = activityDogDetailBinding!!.dogPersonalityTraitsList
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = mViewModel.dogPersonalityTraitAdapter
    }
}