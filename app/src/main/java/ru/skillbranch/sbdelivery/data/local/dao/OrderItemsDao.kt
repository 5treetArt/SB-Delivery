package ru.skillbranch.sbdelivery.data.local.dao

import androidx.room.Dao
import ru.skillbranch.sbdelivery.data.local.base.BaseDao
import ru.skillbranch.sbdelivery.data.local.entities.OrderItem

@Dao
abstract class OrderItemsDao: BaseDao<OrderItem>() {
}