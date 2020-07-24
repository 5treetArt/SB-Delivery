package ru.skillbranch.sbdelivery.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.sbdelivery.App
import ru.skillbranch.sbdelivery.data.delegates.PrefDelegate

object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun clearAll(){
        preferences.edit().clear().apply()
    }


    private var isAuthLive = MutableLiveData(false)
    private var isAuth by PrefDelegate(false)

    fun isAuth(): LiveData<Boolean> = isAuthLive

    @UiThread
    fun setAuth(auth: Boolean) {
        isAuth = auth
        isAuthLive.value = auth
    }


/*
    private var isDarkMode by PrefDelegate(false)
    private var isBigText by PrefDelegate(false)
    private var appSettingsLive = MutableLiveData(AppSettings(isDarkMode ?: false, isBigText ?: false))

    fun getAppSettings(): LiveData<AppSettings> = appSettingsLive

    @UiThread
    fun updateAppSettings(appSettings: AppSettings) {
        isDarkMode = appSettings.isDarkMode
        isBigText = appSettings.isBigText
        appSettingsLive.value = appSettings
    }
    */

}