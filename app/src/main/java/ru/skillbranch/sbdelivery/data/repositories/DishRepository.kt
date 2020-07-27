package ru.skillbranch.sbdelivery.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import ru.skillbranch.sbdelivery.data.local.entities.DishFull
import ru.skillbranch.sbdelivery.data.models.ReviewItemData
import java.util.*

interface IDishRepository {
    fun findDish(dishId: String): LiveData<DishFull>
    fun toggleLike(dishId: String)
    fun isAuth(): LiveData<Boolean>
    fun loadReviewsByRange(size: Int, dishId: String): List<ReviewItemData>
    fun sendReview(dishId: String, text: String)
    fun loadAllReviews(dishId: String, total: Int): ReviewsDataFactory
    fun decrementLike(dishId: String)
    fun incrementLike(dishId: String)
    fun fetchDishContent(dishId: String)
    fun findDishCommentCount(dishId: String): LiveData<Int>
}

object DishRepository : IDishRepository {

    private val dishes = listOf(
        MutableLiveData(
            DishFull(
                id = "5ed8da011f071c00465b2026",
                name = "Бургер \"Америка\"",
                description = "320 г • Котлета из 100% говядины (прожарка medium) на гриле, картофельная булочка на гриле, фирменный соус, лист салата, томат, маринованный лук, жареный бекон, сыр чеддер.",
                image = "https://www.delivery-club.ru/media/cms/relation_product/32350/312372888_m200.jpg",
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

    override fun findDish(dishId: String): LiveData<DishFull> {
        return dishes[0]
    }

    override fun toggleLike(dishId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isAuth(): LiveData<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadReviewsByRange(size: Int, dishId: String): List<ReviewItemData> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendReview(dishId: String, text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loadAllReviews(dishId: String, total: Int): ReviewsDataFactory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun decrementLike(dishId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun incrementLike(dishId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun fetchDishContent(dishId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findDishCommentCount(dishId: String): LiveData<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}