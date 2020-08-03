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
    SELECT id, name, description, poster, old_price, price, category_id, dish_counts.rating as rating, 
     dish_counts.likes_count as likes_count, dish_counts.comments_count as comments_count, 
     dish_personal_infos.is_like as is_like
    FROM dishes 
    INNER JOIN categories ON dishes.category_id = categories.id
    INNER JOIN dish_counts ON dishes.id = dish_counts.dish_id
    INNER JOIN dish_personal_infos ON dishes.id = dish_personal_infos.dish_id
""")
data class DishFull(
    val id: String,
    val name: String,
    val description: String? = null,
    val poster: String? = null,
    @ColumnInfo(name = "old_price")
    val oldPrice: String? = null,
    val price: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: String,
    val rating: Float? = null,
    @ColumnInfo(name = "likes_count")
    val likesCount: Int = 0,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int = 0,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean = false
)
/*
@DatabaseView("""
    SELECT id, name, description, poster, old_price, price, categories.id as category_id,
     categories.name as category_name, categories.`order` as category_order,
     categories.icon as category_icon, categories.parent as category_parent,
     categories.created_at as category_created_at, categories.updated_at as category_updated_at,
     dish_counts.rating as rating, dish_counts.likes_count as likes_count,
     dish_counts.comments_count as comments_count, dish_personal_infos.is_like as is_like
    FROM dishes
    INNER JOIN categories ON dishes.category_id = categories.id
    INNER JOIN dish_counts ON dishes.id = dish_counts.dish_id
    INNER JOIN dish_personal_infos ON dishes.id = dish_personal_infos.dish_id
""")
data class DishFull(
    val id: String,
    val name: String,
    val description: String? = null,
    val poster: String? = null,
    @ColumnInfo(name = "old_price")
    val oldPrice: String? = null,
    val price: Int,
    @Embedded(prefix = "category_")
    val category: Category,
    val rating: Float? = null,
    @ColumnInfo(name = "likes_count")
    val likesCount: Int = 0,
    @ColumnInfo(name = "comments_count")
    val commentsCount: Int = 0,
    @ColumnInfo(name = "is_like")
    val isLike: Boolean = false
)*/

/*
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
)*/