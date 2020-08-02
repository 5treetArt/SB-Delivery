package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["order_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Dish::class,
            parentColumns = ["id"],
            childColumns = ["dish_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    primaryKeys = ["order_id", "dish_id"]
)
data class OrderItem (
    @ColumnInfo(name = "order_id")
    val orderId: String,
    @ColumnInfo(name = "dish_id")
    val dishId: String,
    val name: String,
    val image: String,
    val amount: Int,
    val price: Int
)