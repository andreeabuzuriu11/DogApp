package com.buzuriu.dogapp.services

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

interface ICurrentActivityService {
    fun initWithApplication(application: Application)
    val activity: Activity?
}

class CurrentActivityService : ICurrentActivityService {

    private val activityLifecycleListener: ActivityLifecycleListener = ActivityLifecycleListener()

    override fun initWithApplication(application: Application) {
        application.registerActivityLifecycleCallbacks(activityLifecycleListener)
    }

    override val activity: Activity?
        get() {
            return activityLifecycleListener.activity
        }
}


class ActivityLifecycleListener : Application.ActivityLifecycleCallbacks {

    private var currentActivity: WeakReference<Activity>? = null

    var activity: Activity?
        get() = currentActivity?.get()
        private set(value) {
            currentActivity = if (value != null) {
                WeakReference<Activity>(value)
            } else {
                null
            }
        }


    override fun onActivityCreated(activity: Activity, savedInstance: Bundle?) {
        this.activity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        this.activity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        this.activity = activity
    }

    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityDestroyed(p0: Activity) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityStopped(p0: Activity) {}
}