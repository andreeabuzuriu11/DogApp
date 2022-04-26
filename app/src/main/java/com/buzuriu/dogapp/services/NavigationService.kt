package com.buzuriu.dogapp.services

import android.app.Activity
import android.content.Intent

interface INavigationService {

    fun <T : Activity> navigateToActivity(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean = false
    )

    fun <T : Activity> showOverlay(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean = false,
        parameterName: String? = null,
        parameterValue: String? = null
    )

    fun closeCurrentActivity()


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
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }

    override fun <T : Activity> showOverlay(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean,
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

                    if (finishCurrentActivity) {
                        activity.finish()
                    }
                }
            )
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }

    override fun closeCurrentActivity() {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            activity.finish()
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }

}
