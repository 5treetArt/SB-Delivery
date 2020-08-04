package ru.skillbranch.sbdelivery.data.local.dao

import androidx.room.Dao
import ru.skillbranch.sbdelivery.data.local.base.BaseDao
import ru.skillbranch.sbdelivery.data.local.entities.DishCounts

@Dao
abstract class DishCountsDao: BaseDao<DishCounts>() {
}