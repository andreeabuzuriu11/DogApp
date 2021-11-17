package com.buzuriu.dogapp.views.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.buzuriu.dogapp.viewModels.BaseViewModel

abstract class BaseFragment<out T : BaseViewModel>(vmClass: Class<T>) : Fragment() {

    val mViewModel by lazy { ViewModelProvider(this).get(vmClass) }

    protected abstract val layoutId: Int

    protected open fun layoutInflated(root: View) {}

    private var navHostFragment: Fragment? = null
    private var navHostMain: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(layoutId, container, false)
        layoutInflated(root)

        lifecycle.addObserver(mViewModel)

        return root
    }

    override fun onDestroy() {
        try {

            lifecycle.removeObserver(mViewModel)
        } catch (e: Exception) {
            e.message?.let { Log.d("onDestroyError", it) }
        }
        super.onDestroy()
    }
}

abstract class BaseBoundFragment<out T : BaseViewModel, in K : ViewDataBinding>(vmClass: Class<T>) :
    BaseFragment<T>(vmClass) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<K>(inflater, layoutId, container, false)
            .apply {
                this.lifecycleOwner = this@BaseBoundFragment
            }
        setupDataBinding(binding)

        val root = binding.root
        layoutInflated(root)

        return root
    }

    protected abstract fun setupDataBinding(binding: K)
}