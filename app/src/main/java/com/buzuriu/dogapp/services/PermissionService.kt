package com.buzuriu.dogapp.services

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.buzuriu.dogapp.enums.PermissionResultEnum
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

interface IPermissionService {
    suspend fun requestPermissionStatusAsync(permissionsList: List<String>): Task<List<PermissionResult>>

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissionsList: Array<out String>,
        grantResults: IntArray
    )
}

data class PermissionResult(
    val permissionChecked: String,
    val permissionResultEnum: PermissionResultEnum
)

data class PermissionReq(
    val partialResult: MutableList<PermissionResult>,
    val continuation: Continuation<MutableList<PermissionResult>>
)

class PermissionService(private val activityService: ICurrentActivityService) : IPermissionService {

    private val requestCode: Int = 29
    private val permissionRequests: MutableMap<Int, PermissionReq> = mutableMapOf()

    override suspend fun requestPermissionStatusAsync(permissionsList: List<String>): Task<List<PermissionResult>> {
        val activity = activityService.activity
            ?: throw Exception("The activity is not found")

        checkIfPermissionIsAllowedAndroidManifest(permissionsList)

        return Tasks.forResult(suspendCoroutine<MutableList<PermissionResult>> {

            val partialResponse: MutableList<PermissionResult> = mutableListOf()
            val permissionsListNotGivenYet: MutableList<String> = mutableListOf()

            for (permission in permissionsList) {
                val result = checkPermissionStatus(activity.applicationContext, permission)

                if (result == PermissionResultEnum.Granted) {
                    // the permission has been given
                    partialResponse.add(
                        PermissionResult(
                            permission,
                            PermissionResultEnum.Granted
                        )
                    )
                } else {
                    // the permission is not given yet
                    permissionsListNotGivenYet.add(permission)
                }

            }

            if (permissionsListNotGivenYet.isNotEmpty()) {
                synchronized(this) {
                    permissionRequests[requestCode] = PermissionReq(partialResponse, it)
                }

                ActivityCompat.requestPermissions(
                    activity,
                    permissionsListNotGivenYet.toTypedArray(),
                    requestCode
                )
            } else {
                it.resumeWith(Result.success(partialResponse))
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissionsList: Array<out String>,
        grantResults: IntArray
    ) {
        val request = permissionRequests[requestCode]
        var permissionResultEnum: PermissionResultEnum

        if (request != null) {

            for ((index, _) in permissionsList.withIndex()) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    permissionResultEnum = PermissionResultEnum.Granted
                } else {
                    permissionResultEnum = PermissionResultEnum.Denied
                }

                val permissionName = permissionsList[index]
                request.partialResult.add(PermissionResult(permissionName, permissionResultEnum))
            }

            synchronized(this) {
                permissionRequests.remove(requestCode)
            }

            request.continuation.resumeWith(Result.success(request.partialResult))

        } else {
            throw Exception("We got response for request we never triggert. RequestCode does not match!")
        }
    }

    private fun checkPermissionStatus(
        context: Context,
        permissionChecked: String
    ): PermissionResultEnum {
        return if (ContextCompat.checkSelfPermission(
                context,
                permissionChecked
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionResultEnum.Denied
        } else
            PermissionResultEnum.Granted
    }


    private fun checkIfPermissionIsAllowedAndroidManifest(permissionsList: List<String>) {
        val context = activityService.activity?.applicationContext
            ?: throw Exception("The activity is not found")

        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        if (packageInfo == null) {
            Log.e("ERROR", "Package is empty")
        } else {
            val reqPermissionsInManifest = packageInfo.requestedPermissions
            for (manifestPerm in permissionsList) {
                if (!reqPermissionsInManifest.contains(manifestPerm)) {
                    throw Exception("This permission $manifestPerm is not defined in manifest, please add it")
                }
            }
        }

    }
}



