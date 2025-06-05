package com.android.tripbook.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.tripbook.model.BudgetCategory

@Dao
interface BudgetCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: BudgetCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<BudgetCategory>)

    @Update
    suspend fun update(category: BudgetCategory)

    @Delete
    suspend fun delete(category: BudgetCategory)

    @Query("DELETE FROM budget_categories WHERE tripId = :tripId")
    suspend fun deleteCategoriesForTrip(tripId: String)

    @Query("SELECT * FROM budget_categories WHERE tripId = :tripId ORDER BY name ASC")
    fun getCategoriesForTrip(tripId: String): LiveData<List<BudgetCategory>>

    @Query("SELECT * FROM budget_categories WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Long): LiveData<BudgetCategory?>

    // Potentially a query to get a category by name for a specific trip
    @Query("SELECT * FROM budget_categories WHERE tripId = :tripId AND name = :name LIMIT 1")
    fun getCategoryByNameForTrip(tripId: String, name: String): LiveData<BudgetCategory?>
}
