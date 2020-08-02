package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = Dish::class,
            parentColumns = ["id"],
            childColumns = ["dish_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CartItem(
    @PrimaryKey
    @ColumnInfo(name = "dish_id")
    val dishId: String,
    val amount: Int
)