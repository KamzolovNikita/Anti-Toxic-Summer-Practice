package com.example.android.bellmanford.algorithm

import android.view.View
import android.widget.TextView


data class VertexNeighbour(
    val edge: View,
    val name: String
    //val weight: TextView
)

data class Point(
    val x: Int,
    val y: Int
)

data class VertexInfo(
    val view: View,
    val neighbours: MutableList<VertexNeighbour>,
    val position: Point
)
