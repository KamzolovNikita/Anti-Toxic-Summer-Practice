package com.example.android.bellmanford.algorithm

data class StepData(val ChangedNodeFirst: String,
           val ChangedNodeSecond: String,
           val oldWeight: Int,
           val newWeight: Int)

enum class StepMsg{
    NORMAL,
    NEGATIVE_CYCLE
}

class Step {
    constructor(stepMsg: StepMsg) {
        _stepMsg = stepMsg
    }
    constructor(stepMsg: StepMsg, stepData: StepData) {
        _stepMsg = stepMsg
        _stepData = stepData
    }

    private lateinit var _stepMsg: StepMsg
    private lateinit var _stepData: StepData
}

// A class to represent a connected, directed and weighted graph
class BellmanFord(private val graph: Graph) {

    val stepList = mutableListOf<Step>()

    fun runAlgo(src: String) {
        val dist = mutableMapOf<String, Int>()

        // Step 1: Initialize distances from src to all other vertexes
        graph.adjacencyMap.forEach {
            dist[it.key] = Int.MAX_VALUE
        }
        dist[src] = 0



        // Step 2: Relax all edges |V| - 1 times
        repeat(graph.vertexAmount - 1) {
            graph.adjacencyMap.forEach {
                it.value.forEach { neighbour ->
                    if(dist[it.key] != Int.MAX_VALUE &&
                        dist[it.key]!! + neighbour.second < dist[neighbour.first]!!) {
                            var newStep = Step(StepMsg.NORMAL,
                                StepData(it.key, neighbour.first,
                                dist[neighbour.first]!!,
                                dist[it.key]!! + neighbour.second))
                            stepList.add(newStep)
                            dist[neighbour.first] = dist[it.key]!! + neighbour.second
                    }
                }
            }
        }

        //Step 3: Check for negative cycle
        graph.adjacencyMap.forEach {
            it.value.forEach { neighbour ->
                if(dist[it.key] != Int.MAX_VALUE &&
                    dist[it.key]!! + neighbour.second < dist[neighbour.first]!!) {
                    var newStep = Step(StepMsg.NEGATIVE_CYCLE)
                    stepList.add(newStep)
                    println("Graph contains negative weight cycle")
                    return
                }
            }
        }

        dist.forEach {
            println("${it.key} ${it.value}")
        }
    }
}