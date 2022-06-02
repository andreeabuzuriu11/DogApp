package com.buzuriu.dogapp.services

import android.app.AlertDialog
import com.buzuriu.dogapp.listeners.IClickListener
import com.buzuriu.dogapp.models.AlertBuilderSettings

interface IAlertMessageService {
    fun displayAlertDialog(listOfOptions: AlertBuilderSettings)
    fun displayAlertDialog(
        title: String,
        message: String,
        buttonText: String,
        clickMethod: IClickListener
    )
}

class AlertMessageService(private val activityService: ICurrentActivityService) :
    IAlertMessageService {
    override fun displayAlertDialog(listOfOptions: AlertBuilderSettings) {
        val builder = AlertDialog.Builder(activityService.activity)
        val optionsList = ArrayList<String>()
        for (item in listOfOptions.itemsName!!) {
            optionsList.add(item.toString())
        }

        builder.setItems(listOfOptions.itemsName) { dialogInterface, i ->
            if (optionsList[i] != "Cancel") {
                listOfOptions.itemActions!![optionsList[i]]?.invoke()
            } else {
                dialogInterface.dismiss()
            }
        }

        builder.show()
    }

    override fun displayAlertDialog(
        title: String,
        message: String,
        buttonText: String,
        clickMethod: IClickListener
    ) {
        val builder = AlertDialog.Builder(activityService.activity)
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
    }
}