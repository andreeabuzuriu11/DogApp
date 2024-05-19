package com.buzuriu.dogapp.services

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

interface INavigationService {

    fun <T : Activity> navigateToActivity(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean = false
    )

    fun <T : Activity> showOverlay(
        activityClass: Class<T>,
        finishOldActivity: Boolean = false,
        parameterName: String? = null,
        parameterValue: String? = null
    )

    fun closeCurrentActivity()
    fun closeFragment()
}

class NavigationService(private val currentActivityService: ICurrentActivityService) :
    INavigationService {

    override fun <T : Activity> navigateToActivity(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean
    ) {

        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val intent = Intent(activity, activityClass)

            activity.runOnUiThread(
                Runnable {
                    activity.startActivity(intent)

                    if (finishCurrentActivity) {
                        activity.finish()
                    }
                }
            )
        } else {
            throw Exception("The activity you are trying to navigate to does not exist")
        }
    }

    override fun <T : Activity> showOverlay(
        activityClass: Class<T>,
        finishOldActivity: Boolean,
        parameterName: String?,
        parameterValue: String?
    ) {

        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val intent = Intent(activity, activityClass)

            if (parameterName != null &&
                parameterValue != null
            ) {
                intent.putExtra(parameterName, parameterValue)
            }

            activity.runOnUiThread(
                Runnable {
                    activity.startActivity(intent)

                    if (finishOldActivity) {
                        activity.finish()
                    }
                }
            )
        } else {
            throw Exception("The activity you are trying to navigate to does not exist")
        }
    }

    override fun closeCurrentActivity() {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            activity.finish()
        } else {
            throw Exception("The activity you are trying to close does not exist")
        }
    }


    override fun closeFragment() {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            activity.onBackPressed()
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }
}
