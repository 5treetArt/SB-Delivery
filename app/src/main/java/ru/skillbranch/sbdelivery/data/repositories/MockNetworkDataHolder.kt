package ru.skillbranch.sbdelivery.data.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.sbdelivery.data.local.entities.Review
import ru.skillbranch.sbdelivery.data.remote.res.ReviewRes
import ru.skillbranch.sbdelivery.extensions.toDate
import java.util.*

object MockNetworkDataHolder {
    val reviewsData: MutableMap<String, MutableLiveData<List<ReviewRes>>> = mutableMapOf(
        "5ed8da011f071c00465b2026" to MutableLiveData(
            listOf(
                ReviewRes(
                    dishId = "5ed8da011f071c00465b2026",
                    order = 0,
                    author = "Иван Ильич",
                    date = "2020-07-16T07:50:39.555Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 4,
                    text = "Отличная пицца!",
                    createdAt = Date(1594885839000),
                    updatedAt = Date(1594885839000)
                ),
                ReviewRes(
                    dishId = "5ed8da011f071c00465b2026",
                    order = 1,
                    author = "Иван Ильич",
                    date = "2020-07-16T19:21:31.463Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 1,
                    text = "Съедобный бургер, с послевкусием жирненьких бочков.... Пейте смузи, пацаны!",
                    createdAt = Date(1594927291000),
                    updatedAt = Date(1594927291000)
                ),
                ReviewRes(
                    dishId = "5ed8da011f071c00465b2026",
                    order = 2,
                    author = "Иван Петров",
                    date = "2020-07-24T11:00:41.741Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 4,
                    text = "В целом норм, но могло быть и лучше, честно говоря",
                    createdAt = Date(1595588441000),
                    updatedAt = Date(1595588441000)
                ),
                ReviewRes(
                    dishId = "5ed8da011f071c00465b2026",
                    order = 3,
                    author = "Леша Кабанов",
                    date = "2020-07-24T11:01:39.888Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 5,
                    text = "Отличное блюдо!! Заходит легко - выходит красиво!!",
                    createdAt = Date(1595588499000),
                    updatedAt = Date(1595588499000)
                ),
                ReviewRes(
                    dishId = "5ed8da011f071c00465b2026",
                    order = 4,
                    author = "Леша Кабанов",
                    date = "2020-07-27T10:46:10.374Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 5,
                    text = "Отличное блюдо!! Заходит легко - выходит красиво!!",
                    createdAt = Date(1595846770000),
                    updatedAt = Date(1595846770000)
                )
            )
        )
    )

    fun sendReview(dishId: String, name: String, date: Date, rating: Int, text: String?) {
        val liveData = reviewsData.getOrPut(dishId) { MutableLiveData(emptyList()) }

        liveData.value = liveData.value!! + ReviewRes(
            dishId = dishId,
            order = liveData.value!!.size,
            author = name,
            date = date,
            rating = rating,
            text = text,
            createdAt = date,
            updatedAt = date
        )
    }
}
