package com.buzuriu.dogapp.services

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

interface ISharedPreferencesService {
    fun writeInSharedPref(key: String, myObj: Any)
    fun <T> readFromSharedPref(key: String, objClass: Class<*>): T?
}

class SharedPreferencesService(private val currentActivityService: ICurrentActivityService) :
    ISharedPreferencesService {

    var sharedPref: SharedPreferences? = null
    val activity = currentActivityService.activity
    private val myGson = Gson()

    init {
        sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
    }

    override fun writeInSharedPref(key: String, myObj: Any) {
        val sharedPrefEditor = sharedPref?.edit()
        sharedPrefEditor?.putString(key, getJsonOfT(myObj))
        sharedPrefEditor?.apply()
    }

    override fun <T> readFromSharedPref(key: String, objClass: Class<*>): T? {
        val jsonOfT = sharedPref?.getString(key, "")
        return myGson.fromJson<T>(jsonOfT, objClass)
    }

    private fun getJsonOfT(myObj: Any): String? {
        return myGson.toJson(myObj)
    }

}