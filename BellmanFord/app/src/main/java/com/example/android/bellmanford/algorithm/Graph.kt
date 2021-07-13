package com.example.android.bellmanford.algorithm

typealias Neighbour = Pair<String, Int>
typealias Neighbours = MutableList<Neighbour>


class Graph(val adjacencyMap: Map<String, Neighbours>) {

    var vertexAmount = adjacencyMap.size
    var edgeAmount = 0
    init {
        adjacencyMap.forEach {
            edgeAmount += it.value.size
        }
    }
}
