package ru.skillbranch.sbdelivery.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import ru.skillbranch.sbdelivery.data.local.entities.DishFull
import ru.skillbranch.sbdelivery.data.local.entities.Review
import java.util.*

interface IDishRepository {
    fun findDish(dishId: String): LiveData<DishFull>
    fun isAuth(): LiveData<Boolean>
    fun loadReviewsByRange(position: Int, size: Int, dishId: String): List<Review>
    fun sendReview(dishId: String, name: String, date: Date, rating: Int, text: String?)
    fun allReviews(dishId: String, totalCount: Int): ReviewsDataFactory
    fun toggleLike(dishId: String)
    fun fetchDishContent(dishId: String)
    fun findDishCommentCount(dishId: String): LiveData<Int>
}

object MockDishRepository : IDishRepository {

    private val dishes = listOf(
        MutableLiveData(
            DishFull(
                id = "5ed8da011f071c00465b2026",
                name = "Бургер \"Америка\"",
                description = "320 г • Котлета из 100% говядины (прожарка medium) на гриле, картофельная булочка на гриле, фирменный соус, лист салата, томат, маринованный лук, жареный бекон, сыр чеддер.",
                poster = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m650.jpg",
                oldPrice = "259",
                price = 170,
                rating = 3.8f,
                likes = 1,
                category = "5ed8da011f071c00465b1fde",
                commentsCount = 5,
                createdAt = Date(1591269889000),
                updatedAt = Date(1595846770000)
            )
        )
    )

    private val network = MockNetworkDataHolder

    override fun findDish(dishId: String): LiveData<DishFull> {
        return dishes[0]
    }

    override fun isAuth(): LiveData<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadReviewsByRange(position: Int, size: Int, dishId: String): List<Review> {
        return network.reviewsData
            .getOrElse(dishId) { MutableLiveData(emptyList()) }
            .value!!
            .drop(position)
            .take(size)
    }

    override fun sendReview(dishId: String, name: String, date: Date, rating: Int, text: String?) {
        network.sendReview(dishId, name, date, rating, text)
        //TODO UPDATE local reviews
    }



    override fun allReviews(dishId: String, totalCount: Int) = ReviewsDataFactory(
        itemProvider = ::loadReviewsByRange,
        dishId = dishId,
        totalCount = totalCount
    )

    override fun toggleLike(dishId: String) {
        val dish = dishes[0].value!!
        dishes[0].value = dish.copy(isLike = !dish.isLike)
        //TODO increment or decrement likes count?
    }

    override fun fetchDishContent(dishId: String) {
        //TODO load from network, insert to db
    }

    override fun findDishCommentCount(dishId: String): LiveData<Int> {
        return network.reviewsData.getOrPut(dishId) { MutableLiveData(emptyList()) }.map { it.size }
    }

    suspend fun loadReviewsFromNetwork(
        position: Int,
        size: Int,
        dishId: String
    ): List<Review> {
        return network.reviewsData
            .getOrElse(dishId) { MutableLiveData(emptyList()) }
            .value!!
            .drop(position)
            .take(size)
    }

    suspend fun insertReviewsToDb(items: List<Review>) {

    }
}

class ReviewsDataFactory(
    private val itemProvider: (Int, Int, String) -> List<Review>,
    private val dishId: String,
    private val totalCount: Int
) : DataSource.Factory<Int, Review>() {
    override fun create(): DataSource<Int, Review> = ReviewsDataSource(itemProvider, dishId, totalCount)
}

class ReviewsDataSource(
    private val itemProvider: (Int, Int, String) -> List<Review>,
    private val dishId: String,
    private val totalCount: Int
) : PositionalDataSource<Review>() {
    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Review>) {
        val result = itemProvider(params.startPosition, params.loadSize, dishId)
        callback.onResult(result)
    }

    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<Review>
    ) {
        val result = itemProvider(params.requestedStartPosition, params.requestedLoadSize, dishId)
        callback.onResult(result, params.requestedStartPosition, totalCount)
    }
}
