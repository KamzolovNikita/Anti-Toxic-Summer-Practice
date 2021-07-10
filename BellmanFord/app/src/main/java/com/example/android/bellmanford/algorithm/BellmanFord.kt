package com.example.android.bellmanford.algorithm

data class StepData(val changedNodeFirst: String,
                    val changedNodeSecond: String,
                    val oldWeight: Int,
                    val newWeight: Int)

enum class StepMsg{
    NORMAL,
    PATH,
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
    var sourceVertex = ""
    val dist = mutableMapOf<String, Int>()
    private  val previousVertexForVertex = mutableMapOf<String, String>()
    var containsNegativeCycle = false

    fun runAlgorithm(src: String) {
        dist.clear()
        sourceVertex = src

        // Step 1: Initialize distances from src to all other vertexes
        graph.adjacencyMap.forEach {
            dist[it.key] = Int.MAX_VALUE
        }
        dist[src] = 0
        previousVertexForVertex[src] = src

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
                            previousVertexForVertex[neighbour.first] = it.key
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
                    containsNegativeCycle = true
                    return
                }
            }
        }

        dist.forEach {
            println("${it.key} ${it.value}")
        }

        previousVertexForVertex.forEach {
            println("${it.key} ${it.value}")
        }

        val paths = getPaths()
        paths.forEach { it ->
            println("${it.key}:" +
                    " ${
                        it.value.toString()
                    }")
        }

    }

    fun getPaths(): MutableMap<String, List<String>> {
        val paths = mutableMapOf<String, List<String>>()
        graph.adjacencyMap.forEach {
            paths[it.key] = getSinglePath(it.key)
        }

        return paths
    }

    private fun getSinglePath(vertexTo: String): List<String> {

        if(vertexTo == sourceVertex) return mutableListOf<String>()

        val path = mutableListOf<String>()

        var currentVertex = previousVertexForVertex[vertexTo]
        while(currentVertex != sourceVertex && currentVertex != vertexTo && currentVertex != null && !containsNegativeCycle) {
            path.add(currentVertex.toString())
            currentVertex = previousVertexForVertex[currentVertex]
        }

        if(currentVertex != null)
            path.add(currentVertex.toString())

        return path.reversed()
    }
}