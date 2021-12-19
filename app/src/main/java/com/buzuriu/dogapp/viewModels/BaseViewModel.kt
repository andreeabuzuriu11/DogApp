package com.buzuriu.dogapp.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.buzuriu.dogapp.services.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel : ViewModel(), KoinComponent, LifecycleObserver {

    var isLoadingViewVisible : MutableLiveData<Boolean> = MutableLiveData(false)

    protected val dialogService : IDialogService by inject()
    protected val firebaseAuthService : IFirebaseAuthService by inject()
    protected val navigationService : INavigationService by inject()
    protected val connectivityService : IConnectivityService by inject()
    protected val databaseService : IDatabaseService by inject()
    protected val dataExchangeService: IDataExchangeService by inject()

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

    protected fun ShowLoadingView(isVisible : Boolean)
    {
        viewModelScope.launch(Dispatchers.Main)
        {
            isLoadingViewVisible.value = isVisible
        }
    }
}