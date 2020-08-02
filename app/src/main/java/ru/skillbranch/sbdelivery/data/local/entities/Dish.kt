package ru.skillbranch.sbdelivery.data.local.entities

import androidx.room.*
import java.util.*

@Entity(
    tableName = "dishes",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Dish(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val poster: String,
    @ColumnInfo(name = "old_price")
    val oldPrice: String? = null,
    val price: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Date
)

@DatabaseView("""
    
""")
data class DishItem(
    val id: String,
    val name: String,
    val poster: String? = null,
    @ColumnInfo(name = "old_price")
    val oldPrice: String? = null,
    val price: Int,
    val category: Category,
    val rating: Float? = null,
    @ColumnInfo(name = "likes_count")
    val likesCount: Int = 0,
    //@ColumnInfo(name = "comments_count")
    //val commentsCount: Int = 0,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean = false
)

@DatabaseView("""
    
""")
data class DishFull(
    val id: String,
    val name: String,
    val description: String? = null,
    val poster: String? = null,
    @ColumnInfo(name = "old_price")
    val oldPrice: String? = null,
    val price: Int,
    val rating: Float? = null,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean = false
)