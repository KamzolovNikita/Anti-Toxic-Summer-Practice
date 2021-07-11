package com.example.android.bellmanford.algorithm

typealias Neighbour = Pair<String, Int>
typealias Neighbours = MutableList<Neighbour>


class Graph(val adjacencyMap: Map<String, Neighbours>) {

    // An inner class to represent a weighted edge in graph
    inner class Edge(var src: String, var dest: String, var weight: Double)

    var vertexAmount = adjacencyMap.size
    var edgeAmount = 0
    init {
        adjacencyMap.forEach {
            edgeAmount += it.value.size
        }
    }
}
