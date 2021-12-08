package com.buzuriu.dogapp.views

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.viewModels.SplashViewModel
import com.buzuriu.dogapp.views.base.BaseActivity

class SplashScreen : BaseActivity<SplashViewModel>(SplashViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setContentView(R.layout.activity_splash_screen)
        //initUI() TODO lottie

        super.onCreate(savedInstanceState)

    }
}