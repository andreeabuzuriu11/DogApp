package com.buzuriu.dogapp.views.main.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.buzuriu.dogapp.R
import com.buzuriu.dogapp.utils.LocalDBItems
import com.buzuriu.dogapp.viewModels.OverlayViewModel
import com.buzuriu.dogapp.views.base.BaseActivity

class OverlayActivity : BaseActivity<OverlayViewModel>(OverlayViewModel::class.java) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overlay)

        val fragmentName = intent.getStringExtra(LocalDBItems.fragmentName)

        if (fragmentName != null) {
            try {
                val fragment = Class.forName(fragmentName).newInstance() as Fragment
                supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment)
                    .commit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}