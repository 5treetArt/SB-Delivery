package ru.skillbranch.sbdelivery.data.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.sbdelivery.data.local.entities.Review
import ru.skillbranch.sbdelivery.extensions.toDate
import java.util.*

object MockNetworkDataHolder {
    val reviewsData: MutableMap<String, MutableLiveData<List<Review>>> = mutableMapOf(
        "5ed8da011f071c00465b2026" to MutableLiveData(
            listOf(
                Review(
                    dishId = "5ed8da011f071c00465b2026",
                    author = "Иван Ильич",
                    date = "2020-07-16T07:50:39.555Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 4,
                    text = "Отличная пицца!",
                    createdAt = Date(1594885839000),
                    updatedAt = Date(1594885839000)
                ),
                Review(
                    dishId = "5ed8da011f071c00465b2026",
                    author = "Иван Ильич",
                    date = "2020-07-16T19:21:31.463Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 1,
                    text = "Съедобный бургер, с послевкусием жирненьких бочков.... Пейте смузи, пацаны!",
                    createdAt = Date(1594927291000),
                    updatedAt = Date(1594927291000)
                ),
                Review(
                    dishId = "5ed8da011f071c00465b2026",
                    author = "Иван Петров",
                    date = "2020-07-24T11:00:41.741Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 4,
                    text = "В целом норм, но могло быть и лучше, честно говоря",
                    createdAt = Date(1595588441000),
                    updatedAt = Date(1595588441000)
                ),
                Review(
                    dishId = "5ed8da011f071c00465b2026",
                    author = "Леша Кабанов",
                    date = "2020-07-24T11:01:39.888Z".toDate("yyyy-MM-dd'T'HH:mm:ss.SSSZ")!!,
                    rating = 5,
                    text = "Отличное блюдо!! Заходит легко - выходит красиво!!",
                    createdAt = Date(1595588499000),
                    updatedAt = Date(1595588499000)
                ),
                Review(
                    dishId = "5ed8da011f071c00465b2026",
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

        liveData.value = liveData.value!! + Review(
            dishId = dishId,
            author = name,
            date = date,
            rating = rating,
            text = text,
            createdAt = date,
            updatedAt = date
        )
    }
}
