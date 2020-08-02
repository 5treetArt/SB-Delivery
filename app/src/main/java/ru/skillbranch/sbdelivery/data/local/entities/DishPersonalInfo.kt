package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "dish_personal_infos",
    foreignKeys = [
        ForeignKey(
            entity = Dish::class,
            parentColumns = ["id"],
            childColumns = ["dish_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DishPersonalInfo(
    @PrimaryKey
    @ColumnInfo(name = "dish_id")
    val dishId: String,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean = false,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date = Date()
)