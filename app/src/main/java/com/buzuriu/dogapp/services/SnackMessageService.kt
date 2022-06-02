package com.buzuriu.dogapp.services

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.buzuriu.dogapp.listeners.IClickListener
import com.google.android.material.snackbar.Snackbar

interface ISnackMessageService {
    fun displaySnackBar(stringId: Int, duration: Int = Snackbar.LENGTH_LONG)
    fun displaySnackBar(string: String, duration: Int = Snackbar.LENGTH_LONG)
   /* fun displayAlertDialog(
        title: String,
        message: String,
        buttonText: String,
        clickMethod: IClickListener
    )*/
}

class SnackMessageService(private val currentActivityService: ICurrentActivityService) : ISnackMessageService {

    override fun displaySnackBar(stringId: Int, duration: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val parentLayout = activity.findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, stringId, duration).show()
        }
    }

    override fun displaySnackBar(string: String, duration: Int) {
        val activity: Activity? = currentActivityService.activity

        if (activity != null) {
            val parentLayout = activity.findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, string, duration).show()
        }
    }

    /*override fun displayAlertDialog(
        title: String,
        message: String,
        buttonText: String,
        clickMethod: IClickListener
    ) {
        val builder = AlertDialog.Builder(currentActivityService.activity)
        builder.setTitle(title).setMessage(message).setPositiveButton(
            buttonText
        ) { dialogInterface, _ ->
            clickMethod.clicked()
            dialogInterface?.dismiss()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialogAlert, _ ->
            dialogAlert?.dismiss()
        }
        builder.show()
    }*/
}