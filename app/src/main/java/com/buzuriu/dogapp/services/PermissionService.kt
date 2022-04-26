package com.buzuriu.dogapp.services

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

interface IPermissionService {
    suspend fun requestPermissionStatusAsync(permissions: List<String>): Task<List<PermissionResponse>>

    //needs to be called by the current activity upon  callback for this service to work properly
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}

class PermissionService(private val activityService: ICurrentActivityService) : IPermissionService {

    private val requestCode: Int = 29
    private val permissionRequests: MutableMap<Int, PermissionRequest> = mutableMapOf()


    override suspend fun requestPermissionStatusAsync(permissions: List<String>): Task<List<PermissionResponse>> {
        val activity = activityService.activity
            ?: throw RuntimeException("Can not find current Activity. Please make sure you instantiated and initialized CurrentActivityService")

        ensurePermissionDefinedInManifest(permissions)

        return Tasks.forResult(suspendCoroutine<MutableList<PermissionResponse>> {
            val partialResponse: MutableList<PermissionResponse> = mutableListOf()
            val permissionsStillNeedingGrant: MutableList<String> = mutableListOf()

            for (permission in permissions) {
                val result = checkPermissionStatus(activity.applicationContext, permission)

                if (result == PermissionStatus.Denied) {
                    permissionsStillNeedingGrant.add(permission)
                } else {
                    partialResponse.add(PermissionResponse(permission, PermissionStatus.Granted))
                }
            }

            if (permissionsStillNeedingGrant.size > 0) {
                synchronized(this) {
                    permissionRequests[requestCode] = PermissionRequest(partialResponse, it)
                }

                ActivityCompat.requestPermissions(
                    activity,
                    permissionsStillNeedingGrant.toTypedArray(),
                    requestCode
                )
            } else {
                it.resumeWith(Result.success(partialResponse))
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val request = permissionRequests[requestCode]

        if (request != null) {
            for (i in permissions.indices) {
                val permissionStatus =
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) PermissionStatus.Granted else PermissionStatus.Denied
                val permissionName = permissions[i]

                request.partialResponse.add(PermissionResponse(permissionName, permissionStatus))
            }

            synchronized(this) {
                permissionRequests.remove(requestCode)
            }

            request.continuation.resumeWith(Result.success(request.partialResponse))
        } else {
            throw RuntimeException("We got response for request we never triggert. RequestCode does not match!")
        }
    }

    private fun checkPermissionStatus(
        context: Context,
        permission: String
    ): PermissionStatus {
        val targetsMOrHigher = context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.M

        if (targetsMOrHigher) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return PermissionStatus.Denied
            }
        } else {
            if (PermissionChecker.checkSelfPermission(
                    context,
                    permission
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                return PermissionStatus.Denied
            }
        }

        return PermissionStatus.Granted
    }


    private fun ensurePermissionDefinedInManifest(permissions: List<String>) {
        val context = activityService.activity?.applicationContext
            ?: throw RuntimeException("Can not find current Activity. Please make sure you instantiated and initialized CurrentActivityService")

        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        if (packageInfo != null) {
            val manifestPermissions = packageInfo.requestedPermissions
            for (permission in permissions) {
                if (!manifestPermissions.contains(permission)) {
                    throw RuntimeException("You forgot to add permission $permission to manifest.")
                }
            }
        }
    }

}

enum class PermissionStatus {
    Denied,
    Granted,
}

data class PermissionRequest(
    val partialResponse: MutableList<PermissionResponse>,
    val continuation: Continuation<MutableList<PermissionResponse>>
)

data class PermissionResponse(val permission: String, val permissionStatus: PermissionStatus)