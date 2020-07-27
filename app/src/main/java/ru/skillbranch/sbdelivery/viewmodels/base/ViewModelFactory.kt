@file:Suppress("UNCHECKED_CAST")

package ru.skillbranch.sbdelivery.viewmodels.base

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.skillbranch.sbdelivery.viewmodels.dish.DishViewModel

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle = bundleOf(),
    private val params: Any
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {


    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(DishViewModel::class.java)) {
            return DishViewModel(handle, params as String) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}