package com.buzuriu.dogapp.listeners

import java.lang.Exception

interface IOnCompleteListener {
    fun onComplete(successful : Boolean, exception: Exception?)
}