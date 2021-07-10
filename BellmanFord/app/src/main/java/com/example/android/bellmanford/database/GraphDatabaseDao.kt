package com.example.android.bellmanford.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GraphDatabaseDao {
    @Insert
    fun insert(graph: GraphEntity)

    @Update
    fun update(graph: GraphEntity)

    @Query("SELECT * from graph_table WHERE graphId = :key")
    fun get(key: Long): GraphEntity?

    @Query("DELETE FROM graph_table")
    fun clear()

    @Query("SELECT * FROM graph_table ORDER BY graphId DESC LIMIT 1")
    fun getTonight(): GraphEntity?

    @Query("SELECT * FROM graph_table ORDER BY graphId DESC")
    fun getAllNights(): LiveData<List<GraphEntity>>
}