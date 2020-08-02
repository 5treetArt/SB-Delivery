package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(
    tableName = "reviews",
    primaryKeys = ["dish_id", "order"]
)
data class Review (
    @ColumnInfo(name = "dish_id")
    val dishId: String,
    val order: Int,
    val author: String,
    val date: Date,
    val rating: Int,
    val text: String?,
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)