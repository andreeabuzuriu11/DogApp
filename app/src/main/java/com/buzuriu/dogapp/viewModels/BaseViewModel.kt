package com.buzuriu.dogapp.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.buzuriu.dogapp.services.INavigationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel : ViewModel(), KoinComponent, LifecycleObserver {

    val navigationService : INavigationService by inject()
    var isLoadingViewVisible : MutableLiveData<Boolean> = MutableLiveData(false)

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {
        Log.d("Lifecycle",this.javaClass.name + "onCreate")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume() {
        Log.d("Lifecycle",this.javaClass.name + " onResume")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause() {
        Log.d("Lifecycle",this.javaClass.name + " onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {
        Log.d("Lifecycle",this.javaClass.name + " onStop")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        Log.d("Lifecycle",this.javaClass.name + " onDestroy")
    }
}