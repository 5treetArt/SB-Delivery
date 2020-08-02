package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "dish_counts",
    foreignKeys = [
        ForeignKey(
            entity = Dish::class,
            parentColumns = ["id"],
            childColumns = ["dish_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DishCounts(
    @PrimaryKey
    @ColumnInfo(name = "dish_id")
    val dishId: String,
    val rating: Float? = null,
    @ColumnInfo(name = "likes_count")
    val likesCount: Int = 0,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int = 0,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)