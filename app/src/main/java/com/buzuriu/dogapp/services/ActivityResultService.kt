package com.buzuriu.dogapp.services

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.buzuriu.dogapp.listeners.IGetActivityForResultListener


interface IActivityResultService {

    fun setupActivityForResultLauncher(resultLauncher: ActivityResultLauncher<Intent>)
    fun launchCurrentActivityResultLauncher(intent: Intent, listener: IGetActivityForResultListener)
    fun onActivityForResult(activityResult: ActivityResult)

}

class ActivityResultService() : IActivityResultService {

    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var listener: IGetActivityForResultListener? = null

    override fun setupActivityForResultLauncher(resultLauncher: ActivityResultLauncher<Intent>) {
        this.resultLauncher = resultLauncher
    }

    override fun launchCurrentActivityResultLauncher(
        intent: Intent,
        listener: IGetActivityForResultListener
    ) {
        this.listener = listener
        resultLauncher?.launch(intent)
    }

    override fun onActivityForResult(activityResult: ActivityResult) {
        listener?.activityForResult(activityResult)
    }


}