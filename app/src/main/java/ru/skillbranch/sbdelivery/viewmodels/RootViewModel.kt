package ru.skillbranch.sbdelivery.viewmodels

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.data.repositories.RootRepository
import ru.skillbranch.sbdelivery.viewmodels.base.BaseViewModel
import ru.skillbranch.sbdelivery.viewmodels.base.NavigationCommand

class RootViewModel(handle: SavedStateHandle) : BaseViewModel<RootState>(handle,
    RootState()
) {
    private val repository = RootRepository
    private val privateRoutes = listOf<Int>(/*R.id.nav_profile*/)

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun navigate(command: NavigationCommand) {
        when(command) {
            is NavigationCommand.To ->
                if (privateRoutes.contains(command.destination) && !currentState.isAuth)
                    //set requested destination as arg
                    super.navigate(NavigationCommand.StartLogin(command.destination))
                else super.navigate(command)
            else -> super.navigate(command)
        }
    }
}

data class RootState(
    val isAuth: Boolean = false
) : IViewModelState