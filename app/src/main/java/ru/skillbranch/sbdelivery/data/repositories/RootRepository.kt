package ru.skillbranch.sbdelivery.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.sbdelivery.data.local.PrefManager

object RootRepository {

    private val preferences = PrefManager
    fun isAuth() : LiveData<Boolean> = preferences.isAuth()
    fun setAuth(auth:Boolean) = preferences.setAuth(auth)
}