package com.buzuriu.dogapp.viewModels

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buzuriu.dogapp.services.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.suspendCoroutine

open class BaseViewModel : ViewModel(), KoinComponent {

    var isLoadingViewVisible : MutableLiveData<Boolean> = MutableLiveData(false)

    protected val dialogService : IDialogService by inject()
    protected val firebaseAuthService : IFirebaseAuthService by inject()
    protected val navigationService : INavigationService by inject()
    protected val connectivityService : IConnectivityService by inject()
    protected val databaseService : IDatabaseService by inject()
    protected val dataExchangeService: IDataExchangeService by inject()
    protected val sharedPreferenceService: ISharedPreferencesService by inject()
    protected val alertBuilderService: IAlertBuilderService by inject()
    protected val activityService: ICurrentActivityService by inject()
    protected val permissionService: IPermissionService by inject()
    protected val activityResultService: IActivityResultService by inject()
    protected val storageService: IStorageService by inject()
    protected val localDatabaseService: ILocalDatabaseService by inject()

    protected val currentUser get() = firebaseAuthService.getCurrentUser()
    protected val isSignedIn get() = currentUser != null

    open fun onCreate() {
        Log.d("Lifecycle",this.javaClass.name + "onCreate")
    }

    open fun onResume() {
        Log.d("Lifecycle",this.javaClass.name + " onResume")
    }

    open fun onStart() {
        Log.d("Lifecycle",this.javaClass.name + " onStart")
    }

    open fun onPause() {
        Log.d("Lifecycle",this.javaClass.name + " onPause")
    }

    open fun onStop() {
        Log.d("Lifecycle",this.javaClass.name + " onStop")
    }

    open fun onDestroy() {
        Log.d("Lifecycle",this.javaClass.name + " onDestroy")
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionService.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setupActivityForResultLauncher(resultLauncher: ActivityResultLauncher<Intent>) {
        activityResultService.setupActivityForResultLauncher(resultLauncher)
    }

    fun onActivityForResult(activityResult: ActivityResult)
    {
        activityResultService.onActivityForResult(activityResult)
    }

    protected fun ShowLoadingView(isVisible : Boolean)
    {
        viewModelScope.launch(Dispatchers.Main)
        {
            isLoadingViewVisible.value = isVisible
        }
    }

    protected suspend fun askReadExternalPermission(): Task<Boolean> {
        return Tasks.forResult(suspendCoroutine<Boolean> {
            viewModelScope.launch(Dispatchers.Main) {
                val permissionsResult = permissionService.requestPermissionStatusAsync(
                    listOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ).await()
                var grantedCounter = 0
                for (result in permissionsResult) {
                    if (result.permissionStatus == PermissionStatus.Granted) {
                        grantedCounter++
                    }
                }

                if (grantedCounter == permissionsResult.size) {
                    it.resumeWith(Result.success(true))
                } else {
                    it.resumeWith(Result.success(false))
                }
            }
        })
    }

    protected suspend fun askCameraPermission(): Task<Boolean> {
        return Tasks.forResult(suspendCoroutine<Boolean> {
            viewModelScope.launch(Dispatchers.Main) {
                val permissionsResult = permissionService.requestPermissionStatusAsync(
                    listOf(
                        Manifest.permission.CAMERA)
                ).await()
                var grantedCounter = 0
                for (result in permissionsResult) {
                    if (result.permissionStatus == PermissionStatus.Granted) {
                        grantedCounter++
                    }
                }

                if (grantedCounter == permissionsResult.size) {
                    it.resumeWith(Result.success(true))
                } else {
                    it.resumeWith(Result.success(false))
                }
            }
        })
    }

    protected suspend fun askLocationPermission():Task<Boolean> {
        return Tasks.forResult(suspendCoroutine<Boolean> {
            viewModelScope.launch(Dispatchers.Main) {
                val permissionsResult = permissionService.requestPermissionStatusAsync(
                    listOf(
                        Manifest.permission.ACCESS_FINE_LOCATION)
                ).await()
                var grantedCounter = 0
                for (result in permissionsResult) {
                    if (result.permissionStatus == PermissionStatus.Granted) {
                        grantedCounter++
                    }
                }

                if (grantedCounter == permissionsResult.size) {
                    it.resumeWith(Result.success(true))
                } else {
                    it.resumeWith(Result.success(false))
                }
            }
        })
    }
}