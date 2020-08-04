package ru.skillbranch.sbdelivery.data.repositories

import android.util.Log
import androidx.paging.DataSource
import androidx.sqlite.db.SimpleSQLiteQuery
import ru.skillbranch.sbdelivery.data.local.DbManager.db
import ru.skillbranch.sbdelivery.data.local.entities.DishFull
import java.lang.StringBuilder

object DishesRepository {

    val dishesDao = db.dishesDao()

    fun rawQueryDishes(filter: DishFilter): DataSource.Factory<Int, DishFull> {
        return dishesDao.findDishesByRaw(SimpleSQLiteQuery(filter.toQuery()))
    }
}

class DishFilter(
    val search: String? = null,
    val isLike: Boolean = false,
    val isOnSale: Boolean = false,
    val orderBy: OrderBy = OrderBy.ALPHABET,
    val isDesc: Boolean = true
) {
    fun toQuery(): String {
        val qb = QueryBuilder()

        qb.table("DishFull")

        if (search != null) qb.appendWhere("name LIKE '%$search%'")
        if (isLike) qb.appendWhere("is_like = 1")
        if (isOnSale) qb.appendWhere("old_price IS NOT NULL")
        val column = when(orderBy) {
            OrderBy.ALPHABET -> "name"
            OrderBy.POPULAR -> "likes_count"
            OrderBy.RATING -> "rating"
        }
        qb.orderBy(column, isDesc)
        qb.orderBy("date")
        Log.d("TAG_ArticleFilter", "toQuery: ${qb.build()}")
        return qb.build()
    }

    enum class OrderBy {
        ALPHABET,
        POPULAR,
        RATING
    }
}

class QueryBuilder() {
    private var table: String? = null
    private var selectColumns: String = "*"
    private var joinTables: String? = null
    private var whereCondition: String? = null
    private var order: String? = null

    fun table(table: String) = apply { this.table = table }

    fun orderBy(column: String, isDesc: Boolean = true) = apply {
        order = "ORDER BY $column ${if (isDesc) "DESC" else "ASC"}"
    }

    fun appendWhere(condition: String, logic: String = "AND") = apply {
        if (whereCondition.isNullOrEmpty()) whereCondition = "WHERE $condition "
        else whereCondition += "$logic $condition "
    }

    fun innerJoin(table: String, on: String) = apply {
        if (joinTables.isNullOrEmpty()) joinTables = "INNER JOIN $table ON $on "
        else joinTables += "INNER JOIN $table ON $on "
    }

    fun build(): String {
        check(table != null) { "table must be not null" }
        val strBuilder = StringBuilder("SELECT ")
            .append("$selectColumns ")
            .append("FROM $table ")

        if (joinTables != null) strBuilder.append(joinTables)
        if (whereCondition != null) strBuilder.append(whereCondition)
        if (order != null) strBuilder.append(order)

        return strBuilder.toString()
    }
}