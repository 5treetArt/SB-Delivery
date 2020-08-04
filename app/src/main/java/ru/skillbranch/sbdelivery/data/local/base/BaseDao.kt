package ru.skillbranch.sbdelivery.data.local.base

import androidx.room.*
import ru.skillbranch.sbdelivery.extensions.filterMapIndexed

@Dao
abstract class BaseDao<T> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(obj: T): Long

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(obj: List<T>?): List<Long>

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertBlocking(obj: List<T>?): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract suspend fun update(obj: T)

    /**
     * Update an array of objects from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract suspend fun update(obj: List<T>?)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    abstract suspend fun delete(obj: T)

    @Transaction
    open suspend fun upsert(obj: T) {
        if (insert(obj) == -1L) update(obj)
    }

    @Transaction
    open suspend fun upsert(objList: List<T>) {

        insert(objList)
            .mapIndexed { index, recordResult -> if (recordResult == -1L) objList[index] else null }
            .filterNot { it == null }
            .also { if (it.isNotEmpty()) update(it as List<T>) }

        //val insertResult = insert(objList)
        //
        //val updateList = insertResult.filterMapIndexed( { _, item -> item == -1L } ) { index, item ->
        //    objList[index]
        //}.toList()
        //
        //if (updateList.isNotEmpty()) {
        //    update(updateList)
        //}
    }
}