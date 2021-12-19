package com.buzuriu.dogapp.services

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.findNavController

interface INavigationService {

    fun <T : Activity> navigateToActivity(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean = false
    )

    fun <T : Activity> showOverlay(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean = false,
        parameterName: String? = null,
        parameterValue: String? = null,
    )

    fun closeCurrentActivity()

    fun navigateToFragment(transitionId: Int, navHostId: Int)

    fun popFragmentBackStack(navHostId: Int)

    fun closeCurrentActivityAfterTransition()

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

    override fun navigateToFragment(transitionId: Int, navHostId: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val navController = activity.findNavController(navHostId)
            navController.navigate(transitionId)
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }

    override fun popFragmentBackStack(navHostId: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val navController = activity.findNavController(navHostId)
            navController.popBackStack()
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }

    override fun closeCurrentActivityAfterTransition() {
        val activity: Activity? = currentActivityService.activity
        if (activity != null) {
            activity.finishAfterTransition()
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }
}
