package com.buzuriu.dogapp.views

import android.view.Menu
import android.view.MenuItem
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.databinding.ActivityAccountDetailBinding
import com.buzuriu.dogapp.viewModels.AccountDetailViewModel
import com.buzuriu.dogapp.views.base.BaseBoundActivity

class AccountDetailActivity :
    BaseBoundActivity<AccountDetailViewModel, ActivityAccountDetailBinding>(
        AccountDetailViewModel::class.java
    ) {
    override val layoutId: Int
        get() = R.layout.activity_account_detail

    override fun setupDataBinding(binding: ActivityAccountDetailBinding) {
        binding.viewModel = mViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id: Int = item.getItemId()
        if (id == R.id.edit) {
            mViewModel.editUser()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}