package com.buzuriu.dogapp.listeners

import androidx.activity.result.ActivityResult

interface IGetActivityForResultListener {
    fun activityForResult(activityResult: ActivityResult)
}