package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "orders",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Status::class,
            parentColumns = ["id"],
            childColumns = ["status_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class Order (
    @PrimaryKey
    val id: String,
    val total: Int,
    val address: String,
    @ColumnInfo(name = "status_id")
    val statusId: String,
    val completed: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)