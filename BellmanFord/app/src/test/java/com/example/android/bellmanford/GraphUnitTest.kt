package com.example.android.bellmanford

import com.example.android.bellmanford.algorithm.*
import org.junit.Test
import org.junit.Assert.*

class GraphUnitTest {
    @Test
    fun graphInitIsCorrect() {
        val adjacencyMap = mapOf<String, Neighbours>(
            "A" to mutableListOf<Neighbour>(
                Pair<String, Int>("B", 2),
                Pair<String, Int>("C", 4),
                Pair<String, Int>("D", 6),
            ),
            "B" to mutableListOf<Neighbour>(
                Pair<String, Int>("C", 1),
                Pair<String, Int>("D", 20)
            ),
            "C" to mutableListOf<Neighbour>(
                Pair<String, Int>("A", 1),
                Pair<String, Int>("D", 44)
            ),
            "D" to mutableListOf<Neighbour>(
                Pair<String, Int>("B", -5)
            )
        )
        val graph = Graph(adjacencyMap)

        assertEquals(4, graph.vertexAmount)
        assertEquals(8, graph.edgeAmount)

        assertEquals(adjacencyMap["A"],
            graph.adjacencyMap["A"])

        assertEquals(adjacencyMap["B"],
            graph.adjacencyMap["B"])

        assertEquals(adjacencyMap["C"],
            graph.adjacencyMap["C"])

        assertEquals(adjacencyMap["D"],
            graph.adjacencyMap["D"])
    }
}