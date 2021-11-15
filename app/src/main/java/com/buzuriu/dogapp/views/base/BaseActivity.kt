package com.buzuriu.dogapp.views.base
import android.os.Bundle
import android.util.Log
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
        lifecycle.addObserver(mViewModel)
        super.onCreate(savedInstanceState)

    }

    override fun onDestroy() {
        //TODO error again
        try {
            lifecycle.removeObserver(mViewModel)
            super.onDestroy()
        } catch (e: Exception) {
            Log.d("Error", e.message.toString() + " err")
        }
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