package com.buzuriu.dogapp.viewModels

import android.widget.TimePicker
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.views.SelectDogFragment
import com.buzuriu.dogapp.views.main.ui.OverlayActivity

class AddMeetingViewModel : BaseViewModel() {

    val timePicker : MutableLiveData<TimePicker>? = null
    var dog = MutableLiveData("")

    fun selectDog() {
        navigationService.showOverlay(
            OverlayActivity::class.java,
            false,
            OverlayActivity.fragmentClassNameParam,
            SelectDogFragment::class.qualifiedName
        )
       /* if (!dog.value.isNullOrEmpty())
            dataExchangeService.put(
                SelectDogViewModel::class.qualifiedName!!,
                dog.value.toString()
            )*/
    }
}