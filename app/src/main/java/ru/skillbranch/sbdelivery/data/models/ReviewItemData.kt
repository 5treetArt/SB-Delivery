package ru.skillbranch.sbdelivery.data.models

import java.util.*

data class ReviewItemData(
    val id: String,
    val dishId: String,
    val author: String,
    val rating: Int,
    val text: String? = null,
    val createdAt: Date,
    val updatedAt: Date
)