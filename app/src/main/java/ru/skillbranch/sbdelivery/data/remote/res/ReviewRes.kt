package ru.skillbranch.sbdelivery.data.remote.res

import java.util.*

data class ReviewRes (
    val dishId: String,
    val order: Int,
    val author: String,
    val date: Date,
    val rating: Int,
    val text: String?,
    val isActive: Boolean = true,
    val createdAt: Date,
    val updatedAt: Date
)