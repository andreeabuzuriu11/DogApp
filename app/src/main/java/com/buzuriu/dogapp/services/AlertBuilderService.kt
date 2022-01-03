package com.buzuriu.dogapp.services

import android.app.AlertDialog
import com.buzuriu.dogapp.models.AlertBuilderSettings

interface IAlertBuilderService {
    fun showAlertDialog(listOfOptions: AlertBuilderSettings)
}

class AlertBuilderService(private val activityService: ICurrentActivityService) :
    IAlertBuilderService {
    override fun showAlertDialog(listOfOptions: AlertBuilderSettings) {
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
}