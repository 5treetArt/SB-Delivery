package ru.skillbranch.sbdelivery.viewmodels.dish

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.sbdelivery.data.models.ReviewItemData
import ru.skillbranch.sbdelivery.data.repositories.MockDishRepository
import ru.skillbranch.sbdelivery.data.repositories.ReviewsDataFactory
import ru.skillbranch.sbdelivery.viewmodels.base.BaseViewModel
import ru.skillbranch.sbdelivery.viewmodels.base.IViewModelState
import ru.skillbranch.sbdelivery.viewmodels.base.Notify
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
            state.copy(name = dish.name, isLike = dish.isLike)
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
            .setBoundaryCallback(ReviewsBoundaryCallback(::zeroLoadingHandle, ::itemAtEndLoadingHandle))
            .build()
    }

    private fun itemAtEndLoadingHandle(lastLoadReview: ReviewItemData) {
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.loadReviewsFromNetwork(
                position = listData.value?.positionOffset ?: 0,
                size = listConfig.pageSize,
                dishId = dishId
            )
            if (items.isNotEmpty()) {
                repository.insertReviewsToDb(items)
                //invalidate data in data source -> create new LiveData<PagedList>
                //TODO there is no invalidation now?
                //listData.value?.dataSource?.invalidate()
            }
        }
    }

    private fun zeroLoadingHandle() {

        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.loadReviewsFromNetwork(
                position = 0,
                size = listConfig.initialLoadSizeHint,
                dishId = dishId
            )
            if (items.isNotEmpty()) {
                repository.insertReviewsToDb(items)
                //invalidate data in data source -> create new LiveData<PagedList>
                //TODO there is no invalidation now?
                //listData.value?.dataSource?.invalidate()
            }
        }
    }

    fun handleAmount(amount: Int) {
        updateState { it.copy(amount = amount) }
    }

    fun handleLike() {
        repository.toggleLike(dishId)
    }

    fun handleAdd() {
        //TODO add to cart
        updateState { it.copy(amount = 1) }
        val name = if (currentState.name.isEmpty()) "" else "\"${currentState.name}\" "
        notify(Notify.TextMessage("Блюдо ${name}добавлено в корзину (${currentState.amount} шт.)"))
    }

}

data class DishState(
    val name: String = "",
    val amount: Int = 1,
    val isLike: Boolean = false
) : IViewModelState

class ReviewsBoundaryCallback(
    private val zeroLoadingHandle: () -> Unit,
    private val itemAtEndHandle: (ReviewItemData) -> Unit

) : PagedList.BoundaryCallback<ReviewItemData>() {
    override fun onZeroItemsLoaded() {
        //Storage is empty
        zeroLoadingHandle()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ReviewItemData) {
        //user scroll down -> need load more items
        itemAtEndHandle(itemAtEnd)
    }
}