package com.example.android.bellmanford.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "graph_table")
class GraphEntity {
    @PrimaryKey(autoGenerate = true)
    var graphId: Long = 0L

//    Here goes the graph itself
//    Little example for my friend Nikita <3
//
//    @ColumnInfo(name = "start_time_milli")
//    val startTimeMilli: Long = System.currentTimeMillis(),
//
//    @ColumnInfo(name = "end_time_milli")
//    var endTimeMilli: Long = startTimeMilli,
//
//    @ColumnInfo(name = "quality_rating")
//    var sleepQuality: Int = -1
}