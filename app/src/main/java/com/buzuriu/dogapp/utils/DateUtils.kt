package com.buzuriu.dogapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun getDateString(timeInMillis: Long): String {
            val simpleDateFormat = SimpleDateFormat("EE MMM dd yyyy")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis;
            return simpleDateFormat.format(calendar.time);
        }
        @SuppressLint("SimpleDateFormat")
        fun getTimeString(timeInMillis: Long): String {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis;
            return simpleDateFormat.format(calendar.time);
        }
    }
}