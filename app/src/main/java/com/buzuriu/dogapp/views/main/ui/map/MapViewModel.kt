package com.buzuriu.dogapp.views.main.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.buzuriu.dogapp.viewModels.BaseViewModel

class MapViewModel : BaseViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text
}