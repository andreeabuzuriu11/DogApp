package com.buzuriu.dogapp.services

import android.app.Activity
import android.content.Intent
import androidx.navigation.findNavController

interface INavigationService {

    fun <T : Activity> navigateToActivity(
        activityClass: Class<T>,
        finishCurrentActivity: Boolean = false
    )

    fun closeCurrentActivity()

    fun navigateToFragment(transitionId: Int, navHostId: Int)

    //fun <T : Fragment> navigateToFragment(fragmentClass: KClass<T>, finishCurrentActivity: Boolean)

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
            var navController = activity.findNavController(navHostId)
            navController.navigate(transitionId)
        } else {
            throw Exception("Current activity was null. Please setup ICurrentActivityService correctly.")
        }
    }

    /* override fun <T : Fragment> navigateToFragment(
         fragmentClass: KClass<T>,
         finishCurrentActivity: Boolean
     ) {
         navigateToActivity(
             ContainerActivity::class.java,
             finishCurrentActivity,
             false,
             ContainerActivity.fragmentClassNameParam,
             fragmentClass.qualifiedName
         )
     }*/

    override fun popFragmentBackStack(navHostId: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            var navController = activity.findNavController(navHostId)
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
