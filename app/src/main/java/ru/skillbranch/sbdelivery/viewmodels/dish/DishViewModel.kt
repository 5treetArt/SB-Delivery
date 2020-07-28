package ru.skillbranch.sbdelivery.viewmodels.dish

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.skillbranch.sbdelivery.data.models.ReviewItemData
import ru.skillbranch.sbdelivery.data.repositories.MockDishRepository
import ru.skillbranch.sbdelivery.data.repositories.ReviewsDataFactory
import ru.skillbranch.sbdelivery.viewmodels.base.BaseViewModel
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState
import java.util.concurrent.Executors

class DishViewModel(handle: SavedStateHandle, private val dishId: String) :
    BaseViewModel<DishState>(handle, DishState()) {

    private val repository = MockDishRepository
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
    }

    init {
        subscribeOnDataSource(repository.findDish(dishId)) { dish, state ->
            state.copy(isLike = dish.isLike)
        }
    }

    private val listData: LiveData<PagedList<ReviewItemData>> =
        Transformations.switchMap(repository.findDishCommentCount(dishId)) {
            buildPagedList(repository.allReviews(dishId, it))
        }

    fun observeList(
        owner: LifecycleOwner,
        onChanged: (list: PagedList<ReviewItemData>) -> Unit
    ) {
        listData.observe(owner, Observer { onChanged(it) })
    }

    private fun buildPagedList(dataFactory: ReviewsDataFactory): LiveData<PagedList<ReviewItemData>> {
        return LivePagedListBuilder<Int, ReviewItemData>(dataFactory, listConfig)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun handleAmount(amount: Int) {
        updateState { it.copy(amount = amount) }
    }

    fun handleLike() {
        repository.toggleLike(dishId)
    }

}

data class DishState(
    val amount: Int = 1,
    val isLike: Boolean = false
) : IViewModelState