package ru.skillbranch.sbdelivery.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.sbdelivery.App
import ru.skillbranch.sbdelivery.BuildConfig
import ru.skillbranch.sbdelivery.data.local.dao.*
import ru.skillbranch.sbdelivery.data.local.entities.*

object DbManager {
    val db = Room.databaseBuilder(
        App.applicationContext(),
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()
}

@Database(
    entities = [
        CartItem::class,
        Category::class,
        Dish::class,
        DishCounts::class,
        DishPersonalInfo::class,
        Favorite::class,
        Order::class,
        OrderItem::class,
        Review::class,
        Status::class
    ],
    version = AppDb.DATABASE_VERSION,
    exportSchema = true,
    views = [DishFull::class]
)
@TypeConverters(DateConverter::class)
abstract class AppDb: RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun cartItemsDao(): CartItemsDao
    abstract fun categoriesDao(): CategoriesDao
    abstract fun dishCountsDao(): DishCountsDao
    abstract fun dishesDao(): DishesDao
    abstract fun dishPersonalInfosDao(): DishPersonalInfosDao
    abstract fun favoritesDao(): FavoritesDao
    abstract fun orderItemsDao(): OrderItemsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun reviewsDao(): ReviewsDao
    abstract fun statusesDao(): StatusesDao

}
