package ru.skillbranch.sbdelivery.viewmodels.dish

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.sbdelivery.viewmodels.base.BaseViewModel
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState

class DishViewModel(handle: SavedStateHandle, private val dishId: String) :
    BaseViewModel<DishState>(handle, DishState()) {

}

data class DishState(
    val amount: Int = 1
) : IViewModelState