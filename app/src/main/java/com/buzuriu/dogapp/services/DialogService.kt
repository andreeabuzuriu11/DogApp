package com.buzuriu.dogapp.services

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.listeners.IClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.NonCancellable.cancel

interface IDialogService {
    fun showSnackbar(stringId: Int, duration: Int = Snackbar.LENGTH_LONG)
    fun showSnackbar(string: String, duration: Int = Snackbar.LENGTH_LONG)
    fun showAlertDialog(title: String,
                        message: String,
                        buttonText: String,
                        clickMethod: IClickListener
    )
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

    override fun showAlertDialog(
        title: String,
        message: String,
        buttonText: String,
        clickMethod: IClickListener
    ) {
        val builder = AlertDialog.Builder(currentActivityService.activity)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(
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
    }
}