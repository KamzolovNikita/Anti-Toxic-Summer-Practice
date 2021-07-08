package com.example.android.bellmanford.algorithm

import android.view.View


data class VertexNeighbour(
    val name: String,
    val length: Int
)

data class Point(
    val x: Int,
    val y: Int
)

data class VertexInfo(
    val view: View,
    val neighbours: MutableList<VertexNeighbour>?,
    val position: Point
)