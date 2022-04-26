package com.buzuriu.dogapp.views.base

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.buzuriu.dogapp.viewModels.BaseViewModel

abstract class BaseActivity<out T : BaseViewModel>(vmClass: Class<T>) : AppCompatActivity() {

    val mViewModel by lazy { ViewModelProvider(this).get(vmClass) }

    protected open var activityTitleResourceId: Int? = null
    protected var activitySubtitleResourceId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mViewModel.onCreate()
        setActivityForResultLauncher()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        mViewModel.onResume()
        super.onResume()
    }

    override fun onStart() {
        mViewModel.onStart()
        super.onStart()
    }

    override fun onPause() {
        mViewModel.onPause()
        super.onPause()
    }

    override fun onStop() {
        mViewModel.onStop()
        super.onStop()
    }


    override fun onDestroy() {
        mViewModel.onDestroy()
        super.onDestroy()
    }

    private fun setActivityForResultLauncher() {
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                mViewModel.onActivityForResult(result)
            }

        mViewModel.setupActivityForResultLauncher(resultLauncher)
    }
}

abstract class BaseBoundActivity<out T : BaseViewModel, in K : ViewDataBinding>(vmClass: Class<T>) :
    BaseActivity<T>(vmClass) {

    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<K>(
            this, layoutId
        ).apply {
            this.lifecycleOwner = this@BaseBoundActivity
        }

        setupDataBinding(binding)
    }

    protected abstract fun setupDataBinding(binding: K)
}