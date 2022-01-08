package com.buzuriu.dogapp.views

import android.os.Bundle
import android.view.Menu
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityDogDetailBinding
import com.buzuriu.dogapp.viewModels.DogDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class DogDetailActivity : BaseBoundActivity<DogDetailViewModel, ActivityDogDetailBinding>(
    DogDetailViewModel::class.java
) {
    override val layoutId = R.layout.activity_dog_detail
    override fun setupDataBinding(binding: ActivityDogDetailBinding) {
        binding.viewModel =  mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

}