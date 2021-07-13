package com.example.android.bellmanford

import com.example.android.bellmanford.algorithm.*
import org.junit.Test

import org.junit.Assert.*

class BellmanFordAlgorithmUnitTest {

    /*
     * Testing Bellman-Ford algorithm
     * Graph doesn't have negative cycle
     */
    @Test
    fun bellmanFordAlgorithmIsCorrect() {
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

        val bellmanFord = BellmanFord(graph)
        bellmanFord.runAlgorithm("A")

        assertEquals("A", bellmanFord.sourceVertex)
        assertEquals(mutableMapOf<String, Int>(
            "A" to 0,
            "B" to 1,
            "C" to 2,
            "D" to 6
            ),
            bellmanFord.dist
        )

        val adjacencyMap2 = mapOf<String, Neighbours>(
            "A" to mutableListOf<Neighbour>(
                Pair<String, Int>("B", -1),
                Pair<String, Int>("C", 4),
            ),
            "B" to mutableListOf<Neighbour>(
                Pair<String, Int>("C", 3),
                Pair<String, Int>("D", 2),
                Pair<String, Int>("E", 2)
            ),
            "C" to mutableListOf<Neighbour>(
            ),
            "D" to mutableListOf<Neighbour>(
                Pair<String, Int>("B", 1),
                Pair<String, Int>("C", 5)
            ),
            "E" to mutableListOf<Neighbour>(
                Pair<String, Int>("D", -3)
            )
        )
        val graph2 = Graph(adjacencyMap2)

        val bellmanFord2 = BellmanFord(graph2)
        bellmanFord2.runAlgorithm("A")

        assertEquals("A", bellmanFord2.sourceVertex)
        assertEquals(mutableMapOf<String, Int>(
            "A" to 0,
            "B" to -1,
            "C" to 2,
            "D" to -2,
            "E" to 1
        ),
            bellmanFord2.dist
        )

        val adjacencyMap3 = mapOf<String, Neighbours>(
            "A" to mutableListOf<Neighbour>(
            ),
            "B" to mutableListOf<Neighbour>(
            ),
            "C" to mutableListOf<Neighbour>(
            )
        )

        val graph3 = Graph(adjacencyMap3)

        val bellmanFord3 = BellmanFord(graph3)
        bellmanFord3.runAlgorithm("A")

        assertEquals("A", bellmanFord3.sourceVertex)
        assertEquals(mutableMapOf<String, Int>(
            "A" to 0,
            "B" to Int.MAX_VALUE,
            "C" to Int.MAX_VALUE,
        ),
            bellmanFord3.dist
        )

    }

    /*
     * Testing Bellman-Ford algorithm
     * Graph have negative cycle(s)
     */
    @Test
    fun handleNegativeCycle() {
        val adjacencyMap = mapOf<String, Neighbours>(
            "A" to mutableListOf<Neighbour>(
                Pair<String, Int>("C", -2)
            ),
            "B" to mutableListOf<Neighbour>(
                Pair<String, Int>("A", 4),
                Pair<String, Int>("C", -3)
            ),
            "C" to mutableListOf<Neighbour>(
                Pair<String, Int>("D", 2)
            ),
            "D" to mutableListOf<Neighbour>(
                Pair<String, Int>("B", -1)
            )
        )
        val graph = Graph(adjacencyMap)

        val bellmanFord = BellmanFord(graph)
        bellmanFord.runAlgorithm("A")

        assertEquals(true, bellmanFord.containsNegativeCycle)

    }


}