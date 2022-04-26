package com.buzuriu.dogapp.views.main.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.viewModels.OverlayViewModel
import com.buzuriu.dogapp.views.base.BaseActivity

class OverlayActivity : BaseActivity<OverlayViewModel>(OverlayViewModel::class.java) {
    companion object {
        const val fragmentClassNameParam = "FragmentClassName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay)

        val fragmentClassName = intent.getStringExtra(fragmentClassNameParam)

        if (fragmentClassName != null) {
            try {
                var fragment = Class.forName(fragmentClassName).newInstance() as Fragment
                supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //elimin animatia de slide left
        overridePendingTransition(0, 0);
    }

    override fun onDestroy() {
        super.onDestroy()
        //elimin animatia de slide left
        overridePendingTransition(0, 0);
    }

}