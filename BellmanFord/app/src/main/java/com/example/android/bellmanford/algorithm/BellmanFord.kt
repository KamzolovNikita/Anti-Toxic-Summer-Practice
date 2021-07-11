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
    constructor(stepNumber: Int, stepMsg: StepMsg) {
        this.stepNumber = stepNumber
        this.stepMsg = stepMsg
    }
    constructor(stepNumber: Int, stepMsg: StepMsg, stepData: StepData) {
        this.stepNumber = stepNumber
        this.stepMsg = stepMsg
        this.stepData = stepData
    }

    var stepNumber = -1
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
    private val negativeCycleList = mutableListOf<String>()

    fun runAlgorithm(src: String) {
        dist.clear()
        negativeCycleList.clear()
        currentStep = 0
        stepsLeft = 0
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
                            val newStep = Step(
                                currentStep + 1,
                                StepMsg.NORMAL,
                                StepData(it.key, neighbour.first,
                                    distanceForSecondNode,
                                    distanceToFirstNode + neighbour.second))
                            stepList.add(newStep)
                            currentStep++
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
                    val newStep = Step(
                        currentStep + 1,
                        StepMsg.NEGATIVE_CYCLE
                    )

                    var currentVertex : String? = it.key

                    repeat(graph.vertexAmount - 1) {
                        currentVertex = previousVertexForVertex[currentVertex]
                    }

                    var cycleVertex = currentVertex

                    while(cycleVertex != previousVertexForVertex[currentVertex]) {
                        previousVertexForVertex[currentVertex]?.let{ it -> negativeCycleList.add(it)}
                        currentVertex = previousVertexForVertex[currentVertex]!!
                    }
                    cycleVertex?.let { it1 -> negativeCycleList.add(it1) }


                    stepList.add(newStep)
                    currentStep = 0
                    stepsLeft = stepList.size
                    containsNegativeCycle = true
                    return
                }
            }
        }

        graph.adjacencyMap.forEach {
            val newStep = Step(
                currentStep + 1,
                StepMsg.PATH,
                StepData(sourceVertex,
                it.key,
                dist[it.key] ?: 0,
                null)
            )
            currentStep++
            stepList.add(newStep)
        }

        currentStep = 0
        stepsLeft = stepList.size
    }

    fun getSteps(): List<Step> {
        println("Trying to step : curStep ${currentStep}, stepsLeft ${stepsLeft}")
        println("After doing step: curStep ${currentStep + 1}, stepsLeft ${stepsLeft - 1}")
        stepsLeft--
        return stepList.slice(IntRange(0, currentStep++))
    }

    fun getAllSteps(): List<Step> {
        currentStep = stepList.size
        stepsLeft = 0
        return stepList
    }

    fun stepBack() {
        println("Trying to step back: curStep ${currentStep}, stepsLeft ${stepsLeft}")
        if(currentStep > 0) {
            stepsLeft++
            currentStep--
        }
        println("After step back: curStep ${currentStep}, stepsLeft ${stepsLeft}")
    }

    fun getPath(vertexName: String): List<String> {
        return getSinglePath(vertexName)
    }

    fun hasNext(): Boolean {
        println("In hasNext: stepsLeft=${stepsLeft}")
        return stepsLeft > 0
    }

    fun getNegativeCycle(): List<String> {
        return negativeCycleList.reversed()
    }

    private fun getPaths(): MutableMap<String, List<String>> {
        val paths = mutableMapOf<String, List<String>>()
        graph.adjacencyMap.forEach {
            paths[it.key] = getSinglePath(it.key)
        }

        return paths
    }

    private fun getSinglePath(vertexTo: String): List<String> {
        val path = mutableListOf<String>()
        path.add(vertexTo)

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