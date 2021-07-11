package com.example.android.bellmanford.algorithm

import android.view.View
import android.widget.TextView


data class VertexNeighbour(
    val edgeView: View,
    val firstArrowPetalView: View,
    val secondArrowPetalView: View,
    val name: String,
    val weightView: TextView
)

data class Point(
    val x: Int,
    val y: Int
)

data class VertexInfo(
    val vertexView: View,
    val neighbours: MutableList<VertexNeighbour>,
    val position: Point
)
