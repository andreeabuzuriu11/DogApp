package com.buzuriu.dogapp.views

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.viewModels.ProgressBarViewModel
import com.buzuriu.dogapp.views.base.BaseActivity

@SuppressLint("ProgressBar")
class ProgressBarActivity : BaseActivity<ProgressBarViewModel>(ProgressBarViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.activity_progress_bar)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        super.onCreate(savedInstanceState)

    }
}