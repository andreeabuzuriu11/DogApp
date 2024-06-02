package com.buzuriu.dogapp.services

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase


interface IFirebaseAnalyticsService {
    fun getCurrentAnalytics(): FirebaseAnalytics
    fun logEvent(event: String, category: String, value: String)

}

class FirebaseAnalyticsService : IFirebaseAnalyticsService {
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    override fun getCurrentAnalytics(): FirebaseAnalytics {
        return analytics
    }

    override fun logEvent(event: String, category: String, categoryValue: String) {
        val bundle = Bundle()
        bundle.putString(category, categoryValue)
        // bundle.putString("sub_Cat", "sub_CatValue")
        analytics.logEvent(event, bundle)
    }

}