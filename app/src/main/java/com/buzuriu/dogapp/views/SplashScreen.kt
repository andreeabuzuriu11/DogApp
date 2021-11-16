package com.buzuriu.dogapp.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.viewModels.SplashViewModel
import com.buzuriu.dogapp.views.base.BaseActivity

class SplashScreen : BaseActivity<SplashViewModel>(SplashViewModel::class.java) {
    override fun onCreate(savedInstanceState: Bundle?) {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash_screen)
        //initUI() TODO lottie

        super.onCreate(savedInstanceState)

    }
}