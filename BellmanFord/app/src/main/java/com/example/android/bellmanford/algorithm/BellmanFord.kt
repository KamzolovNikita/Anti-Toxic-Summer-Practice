package com.example.android.bellmanford.algorithm


data class StepData(
    val firstVertexParam: String,
    val secondVertexParam: String,
    val firstWeightParam: Int,
    val secondWeightParam: Int?
)

enum class StepMsg{
    NORMAL,
    PATH,
    NEGATIVE_CYCLE
}

class Step {
    constructor(stepMsg: StepMsg) {
        this.stepMsg = stepMsg
    }
    constructor(stepMsg: StepMsg, stepData: StepData) {
        this.stepMsg = stepMsg
        this.stepData = stepData
    }

    lateinit var stepMsg: StepMsg
    lateinit var stepData: StepData
}

// A class to represent a connected, directed and weighted graph
class BellmanFord(private val graph: Graph) {

    private val stepList = mutableListOf<Step>()
    var sourceVertex = ""
    val dist = mutableMapOf<String, Int>()
    private  val previousVertexForVertex = mutableMapOf<String, String>()
    var containsNegativeCycle = false

    private var stepsLeft = graph.adjacencyMap.size
    private var currentStep = 0

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

                    val distanceToFirstNode = if (dist[it.key] != null) dist[it.key]!! else 0
                    val distanceForSecondNode = if (dist[neighbour.first] != null) dist[neighbour.first]!! else 0

                    if(distanceToFirstNode != Int.MAX_VALUE &&
                        distanceToFirstNode + neighbour.second < distanceForSecondNode) {
                            val newStep = Step(StepMsg.NORMAL,
                                StepData(it.key, neighbour.first,
                                    distanceForSecondNode,
                                    distanceToFirstNode + neighbour.second))
                            stepList.add(newStep)
                            ++stepsLeft
                            dist[neighbour.first] = dist[it.key]!! + neighbour.second
                            previousVertexForVertex[neighbour.first] = it.key
                    }
                }
            }
        }

        //Step 3: Check for negative cycle
        graph.adjacencyMap.forEach {
            it.value.forEach { neighbour ->

                val distanceToFirstNode = if (dist[it.key] != null) dist[it.key]!! else 0
                val distanceForSecondNode = if (dist[neighbour.first] != null) dist[neighbour.first]!! else 0

                if(distanceToFirstNode != Int.MAX_VALUE &&
                    distanceToFirstNode + neighbour.second < distanceForSecondNode) {
                    val newStep = Step(StepMsg.NEGATIVE_CYCLE)
                    stepList.add(newStep)
                    ++stepsLeft
                    println("Graph contains negative weight cycle")
                    containsNegativeCycle = true
                    return
                }
            }
        }

        graph.adjacencyMap.forEach {
            val newStep = Step(StepMsg.PATH,
                StepData(sourceVertex,
                it.key,
                dist[it.key] ?: 0,
                null)
            )
            stepList.add(newStep)
        }

    }

    fun getSteps(): List<Step> {
        stepsLeft--
        return stepList.slice(IntRange(0, currentStep++))
    }

    fun stepBack() {
        stepsLeft++
        currentStep--
    }

    fun getPath(vertexName: String): List<String> {
        return getSinglePath(vertexName)
    }

    fun hasNext(): Boolean {
        return stepsLeft > 0
    }

    private fun getPaths(): MutableMap<String, List<String>> {
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