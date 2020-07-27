package ru.skillbranch.sbdelivery.data.local.entities

import java.util.*

data class DishFull(
    val id: String,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val oldPrice: String? = null,
    val price: Int,
    val rating: Float? = null,
    val likes: Int = 0,
    val category: String,
    val commentsCount: Int = 0,
    val createdAt: Date,
    val updatedAt: Date,
    val isLike: Boolean = false,
    val isOnSale: Boolean = false
)