package com.buzuriu.dogapp.services

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar

interface IDialogService {
    fun showSnackbar(stringId: Int, duration: Int = Snackbar.LENGTH_LONG)
    fun showSnackbar(string: String, duration: Int = Snackbar.LENGTH_LONG)
}

class DialogService(private val currentActivityService: ICurrentActivityService) : IDialogService {

    override fun showSnackbar(stringId: Int, duration: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val parentLayout = activity.findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, stringId, duration).show()
        }
    }

    override fun showSnackbar(string: String, duration: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val parentLayout = activity.findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, string, duration).show()
        }
    }
}