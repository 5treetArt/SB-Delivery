package ru.skillbranch.sbdelivery

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

class App : Application() {

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        //TODO set default Night Mode

        Stetho.initializeWithDefaults(this)
    }
}