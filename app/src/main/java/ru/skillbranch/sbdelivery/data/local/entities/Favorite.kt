package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = Dish::class,
            parentColumns = ["id"],
            childColumns = ["dish_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Favorite(
    @PrimaryKey
    @ColumnInfo(name = "dish_id")
    val dishId: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)