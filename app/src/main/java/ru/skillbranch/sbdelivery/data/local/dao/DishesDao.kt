package ru.skillbranch.sbdelivery.data.local.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.skillbranch.sbdelivery.data.local.base.BaseDao
import ru.skillbranch.sbdelivery.data.local.entities.Dish
import ru.skillbranch.sbdelivery.data.local.entities.DishFull

@Dao
abstract class DishesDao : BaseDao<Dish>() {

    @RawQuery(observedEntities = [DishFull::class])
    abstract fun findDishesByRaw(simleSQLiteQuery: SimpleSQLiteQuery): DataSource.Factory<Int, DishFull>
}