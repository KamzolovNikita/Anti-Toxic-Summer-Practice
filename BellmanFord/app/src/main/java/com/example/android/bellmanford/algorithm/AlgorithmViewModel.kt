package com.example.android.bellmanford.algorithm

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.marginLeft
import androidx.core.view.marginTop
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.bellmanford.R
import java.lang.Math.*
import java.util.*
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

enum class EdgeSpawnStates {
    ALREADY_EXIST,
    OPPOSITE_EXIST,
    NOTHING_EXIST
}

class AlgorithmViewModel : ViewModel() {


    //region onClick events
    private val _eventEdgeAlreadyExistError = MutableLiveData<Boolean>()
    val eventEdgeAlreadyExistError: LiveData<Boolean>
        get() = _eventEdgeAlreadyExistError

    fun onEdgeAlreadyExistError() {
        _eventEdgeAlreadyExistError.value = true
    }

    fun onEdgeAlreadyExistErrorFinish() {
        _eventEdgeAlreadyExistError.value = false
    }

    private val _eventBackNavigate = MutableLiveData<Boolean>()
    val eventBackNavigate: LiveData<Boolean>
        get() = _eventBackNavigate

    fun onBackNavigate() {
        _eventBackNavigate.value = true
    }

    fun onBackNavigateFinish() {
        _eventBackNavigate.value = false
    }

    private val _eventAlgorithmInfoShow = MutableLiveData<Boolean>()
    val eventAlgorithmInfoShow: LiveData<Boolean>
        get() = _eventAlgorithmInfoShow

    fun onAlgorithmInfoShow() {
        _eventAlgorithmInfoShow.value = true
    }

    fun onAlgorithmInfoShowFinish() {
        _eventAlgorithmInfoShow.value = false
    }

    private val _eventAlgorithmStepShow = MutableLiveData<Boolean>()
    val eventAlgorithmStepShow: LiveData<Boolean>
        get() = _eventAlgorithmStepShow

    fun onAlgorithmStepShow() {
        _eventAlgorithmStepShow.value = true
    }

    fun onAlgorithmStepShowFinish() {
        _eventAlgorithmStepShow.value = false
    }

    private val _eventVertexAlreadyExist = MutableLiveData<Boolean>()
    val eventVertexAlreadyExist: LiveData<Boolean>
        get() = _eventVertexAlreadyExist


    fun onVertexAlreadyExistEvent() {
        _eventVertexAlreadyExist.value = true
    }

    fun onVertexAlreadyExistEventFinish() {
        _eventVertexAlreadyExist.value = false
    }

    private val _eventAlgorithmReady = MutableLiveData<Boolean>()
    val eventAlgorithmReady: LiveData<Boolean>
        get() = _eventAlgorithmReady

    fun onAlgorithmReady() {
        _eventAlgorithmReady.value = true
    }

    fun onAlgorithmFinish() {
        _eventAlgorithmReady.value = false
    }

    //endregion

    private lateinit var graph: Graph

    private val _pressedVertices = MutableLiveData<Pair<View?, View?>>()
    val pressedVertices: LiveData<Pair<View?, View?>>
        get() = _pressedVertices


    private var vertexDiameter = 0
    private var edgeLineHeight = 0
    private var edgeArrowHeight = 0
    private var edgeArrowWidth = 0
    private var edgeWeightTextSize = 0
    @DrawableRes private var defaultEdgeDrawable = 0
    @DrawableRes private var defaultVertexDrawable = 0
    @DrawableRes private var highlightedEdgeDrawable = 0
    @DrawableRes private var highlightedVertexDrawable = 0

    private val adjacencyList = mutableMapOf<String, VertexInfo>()
    private lateinit var bellmanFordAlgorithm: BellmanFord

    private val _algorithmSteps = MutableLiveData<List<Step>>()
    val algorithmSteps: LiveData<List<Step>>
        get() = _algorithmSteps

    private var startVertexName = ""

    private var highlightedPath = listOf<String>()

    var isEditing = true

    fun initDimensions(context: Context) {
        vertexDiameter =
            context.resources.getDimension(R.dimen.size_fragment_algorithm_vertex).toInt()
        edgeLineHeight =
            context.resources.getDimension(R.dimen.height_fragment_algorithm_edge).toInt()
        edgeArrowHeight =
            context.resources.getDimension(R.dimen.height_fragment_algorithm_arrow).toInt()
        edgeArrowWidth =
            context.resources.getDimension(R.dimen.width_fragment_algorithm_arrow).toInt()
        edgeWeightTextSize =
            context.resources.getDimension(R.dimen.text_size_fragment_algorithm_edge_weight).toInt()
        defaultEdgeDrawable = R.drawable.view_line
        defaultVertexDrawable = R.drawable.img_graph_vertex
        highlightedEdgeDrawable = R.drawable.view_line_highlighted
        highlightedVertexDrawable = R.drawable.img_graph_vertex_selected
    }

    //region vertex creating
    fun setupVertex(
        vertexView: AppCompatButton,
        xClick: Int,
        yClick: Int,
        vertexName: String
    ): Boolean {
        setViewLayoutParams(
            vertexView,
            vertexDiameter,
            vertexDiameter,
            xClick - vertexDiameter / 2,
            yClick - vertexDiameter / 2
        )
        vertexView.isClickable = true
        vertexView.setBackgroundResource(R.drawable.img_graph_vertex)

        vertexView.text = vertexName.replace(" ", "").toUpperCase(Locale.ROOT).take(4)
        if (adjacencyList.containsKey(vertexView.text)) {
            onVertexAlreadyExistEvent()
            return false
        }
        initVertexInAdjacencyList(vertexView)

        vertexView.setOnClickListener {
            vertexOnClickListener(it as AppCompatButton)
        }
        return true
    }

    private fun vertexOnClickListener(vertexView: AppCompatButton) {
        if(isEditing) {
            val tempPair = _pressedVertices.value ?: Pair(null, null)
            when (vertexView) {
                tempPair.first -> {
                    vertexView.setBackgroundResource(R.drawable.img_graph_vertex)
                    _pressedVertices.value = Pair(tempPair.second, null)
                }
                tempPair.second -> {
                    vertexView.setBackgroundResource(R.drawable.img_graph_vertex)
                    _pressedVertices.value = Pair(tempPair.first, null)
                }

                else -> {
                    vertexView.setBackgroundResource(R.drawable.img_graph_vertex_selected)
                    tempPair.second?.setBackgroundResource(R.drawable.img_graph_vertex)
                    _pressedVertices.value = Pair(vertexView, tempPair.first)

                }
            }
        }
        else {
            if(startVertexName == "") {
                initGraph()
                bellmanFordAlgorithm = BellmanFord(graph)
                bellmanFordAlgorithm.runAlgorithm(vertexView.text.toString())
                startVertexName = vertexView.text.toString()
                vertexView.setBackgroundResource(highlightedVertexDrawable)
                onAlgorithmReady()
                nextAlgorithmStep()
            }
        }
    }

    private fun initVertexInAdjacencyList(vertex: AppCompatButton) {
        val params = vertex.layoutParams as FrameLayout.LayoutParams
        val center =
            Point(params.leftMargin + vertexDiameter / 2, params.topMargin + vertexDiameter / 2)
        adjacencyList[vertex.text.toString()] = VertexInfo(vertex, mutableListOf(), center)
    }
    //endregion

    fun editingMode() {
        adjacencyList[startVertexName]?.vertexView?.setBackgroundResource(defaultVertexDrawable)
        startVertexName = ""
        changePathColor(highlightedPath, defaultVertexDrawable, defaultEdgeDrawable)
        _algorithmSteps.value = listOf()
        onAlgorithmFinish()
    }

    fun algorithmMode() {
        clearPressedVertices()
    }

    //region edge creating
    fun setupEdge(
        edgeView: View,
        firstArrowPetalView: View,
        secondArrowPetalView: View,
        firstVertexView: AppCompatButton,
        secondVertexView: AppCompatButton,
        edgeWeightView: TextView
    ): Boolean {
        val vertexInfo1: VertexInfo = adjacencyList.getOrElse(firstVertexView.text.toString(), {
            return false
        })
        val vertexInfo2: VertexInfo = adjacencyList.getOrElse(secondVertexView.text.toString(), {
            return false
        })

        val point1 = vertexInfo1.position
        val point2 = vertexInfo2.position

        val length = findEdgeLength(point1, point2)
        val rotation = findEdgeRotation(point1, point2)

        val edgeSpawnState =
            checkEdgesSpawnState(
                vertexInfo1,
                vertexInfo2,
                firstVertexView.text.toString(),
                secondVertexView.text.toString()
            )

        if (edgeSpawnState == EdgeSpawnStates.ALREADY_EXIST) return false

        initLine(edgeView, point1, length, rotation)
        initArrowPetal(firstArrowPetalView, point2, rotation, 30F)
        initArrowPetal(secondArrowPetalView, point2, rotation, -30F)
        initWeight(edgeWeightView, edgeView, rotation, length)

        val newVertexNeighbour = VertexNeighbour(
            edgeView,
            firstArrowPetalView,
            secondArrowPetalView,
            secondVertexView.text.toString(),
            edgeWeightView
        )

        initNeighbourInAdjacencyList(newVertexNeighbour, firstVertexView.text.toString())
        println(adjacencyList)

        if (edgeSpawnState == EdgeSpawnStates.OPPOSITE_EXIST) {
            getOppositeEdgeView(firstVertexView.text.toString(), vertexInfo2)?.let {
                moveEdgeViewsToFit(newVertexNeighbour, it)
            }
        }
        return true
    }

    private fun checkEdgesSpawnState(
        vertexInfo1: VertexInfo,
        vertexInfo2: VertexInfo,
        firstVertexName: String,
        secondVertexName: String
    ): EdgeSpawnStates {
        vertexInfo1.neighbours.forEach {
            if (it.name == secondVertexName)
                return EdgeSpawnStates.ALREADY_EXIST
        }
        vertexInfo2.neighbours.forEach {
            if (it.name == firstVertexName)
                return EdgeSpawnStates.OPPOSITE_EXIST
        }
        return EdgeSpawnStates.NOTHING_EXIST
    }

    private fun getOppositeEdgeView(
        oppositeVertexName: String,
        vertexInfo: VertexInfo
    ): VertexNeighbour? {
        vertexInfo.neighbours.forEach {
            if (it.name == oppositeVertexName) {
                return it
            }
        }
        return null
    }

    private fun initNeighbourInAdjacencyList(
        vertexNeighbour: VertexNeighbour,
        firstVertexName: String
    ) {
        adjacencyList[firstVertexName]?.neighbours?.add(vertexNeighbour)
    }

    private fun initWeight(
        edgeWeightView: TextView,
        edgeView: View,
        rotation: Float,
        edgeLength: Int
    ) {
        val offset = calculateOffsetBasedOnAngle(edgeLength / 2, rotation.toDouble(), false)

        setViewLayoutParams(
            edgeWeightView,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            edgeView.marginLeft + offset.x,
            edgeView.marginTop + offset.y
        )

        edgeWeightView.pivotX = 0F
        edgeWeightView.isClickable = true

        if (abs(rotation) > 90) edgeWeightView.rotation = 180 + rotation
        else edgeWeightView.rotation = rotation

        edgeWeightView.setTextSize(TypedValue.COMPLEX_UNIT_PX, edgeWeightTextSize.toFloat())
    }

    private fun calculateOffsetBasedOnAngle(
        radius: Int,
        rotation: Double,
        inverse: Boolean
    ): Point {
        val offsetX = radius * kotlin.math.cos(toRadians(rotation))
        val offsetY = radius * kotlin.math.sin(toRadians(rotation))
        return when (inverse) {
            false -> Point(offsetX.toInt(), offsetY.toInt())
            true -> Point(offsetY.toInt(), offsetX.toInt())
        }
    }

    private fun changeMargins(point: Point, view: View) {
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.setMargins(
            point.x,
            point.y,
            0,
            0
        )
    }


    private fun moveEdgeViewsToFit(firstEdge: VertexNeighbour, secondEdge: VertexNeighbour) {
        val firstOffset = calculateOffsetBasedOnAngle(
            vertexDiameter * 3 / 8,
            firstEdge.edgeView.rotation.toDouble(),
            true
        )
        val secondOffset = calculateOffsetBasedOnAngle(
            vertexDiameter * 3 / 8,
            secondEdge.edgeView.rotation.toDouble(),
            true
        )

        //move lines
        changeMargins(
            Point(
                firstEdge.edgeView.marginLeft - firstOffset.x,
                firstEdge.edgeView.marginTop + firstOffset.y
            ), firstEdge.edgeView
        )

        changeMargins(
            Point(
                secondEdge.edgeView.marginLeft - secondOffset.x,
                secondEdge.edgeView.marginTop + secondOffset.y
            ), secondEdge.edgeView
        )


        //move arrow
        changeMargins(
            Point(
                firstEdge.firstArrowPetalView.marginLeft - firstOffset.x,
                firstEdge.firstArrowPetalView.marginTop + firstOffset.y,
            ), firstEdge.firstArrowPetalView
        )

        changeMargins(
            Point(
                secondEdge.firstArrowPetalView.marginLeft - secondOffset.x,
                secondEdge.firstArrowPetalView.marginTop + secondOffset.y,
            ), secondEdge.firstArrowPetalView
        )

        changeMargins(
            Point(
                firstEdge.secondArrowPetalView.marginLeft - firstOffset.x,
                firstEdge.secondArrowPetalView.marginTop + firstOffset.y,
            ), firstEdge.secondArrowPetalView
        )

        changeMargins(
            Point(
                secondEdge.secondArrowPetalView.marginLeft - secondOffset.x,
                secondEdge.secondArrowPetalView.marginTop + secondOffset.y,
            ), secondEdge.secondArrowPetalView
        )

        //move weight
        changeMargins(
            Point(
                firstEdge.weightView.marginLeft - firstOffset.x,
                firstEdge.weightView.marginTop + firstOffset.y,
            ), firstEdge.weightView
        )

        changeMargins(
            Point(
                secondEdge.weightView.marginLeft - secondOffset.x,
                secondEdge.weightView.marginTop + secondOffset.y,
            ), secondEdge.weightView
        )
    }


    private fun initLine(edge: View, point: Point, length: Int, rotation: Float) {
        setViewLayoutParams(
            edge,
            length,
            edge.context.resources.getDimension(R.dimen.height_fragment_algorithm_edge).toInt(),
            point.x,
            point.y
        )
        edge.isClickable = true
        edge.pivotX = 0F
        edge.pivotY = edge.resources.getDimension(R.dimen.height_fragment_algorithm_edge) / 2
        edge.rotation = rotation


        edge.setBackgroundResource(R.drawable.view_line)
    }

    private fun initArrowPetal(
        arrowPetal: View,
        point: Point,
        rotation: Float,
        additionalRotation: Float
    ) {
        val offset =
            calculateOffsetBasedOnAngle(vertexDiameter / 2, rotation.toDouble(), false)
        setViewLayoutParams(
            arrowPetal,
            edgeArrowWidth,
            edgeArrowHeight,
            point.x - offset.x,
            point.y - offset.y
        )
        arrowPetal.pivotY = (edgeArrowHeight / 2).toFloat()
        arrowPetal.pivotX = 0F
        arrowPetal.rotation = rotation + additionalRotation + 180
        arrowPetal.setBackgroundResource(R.drawable.view_line)
    }


    private fun findEdgeRotation(point1: Point, point2: Point): Float {
        val triangleEdgeLength1 = abs(point1.x - point2.x).toDouble()
        val triangleEdgeLength2 = abs(point1.y - point2.y).toDouble()

        val angleTan = triangleEdgeLength2 / triangleEdgeLength1
        val angle = toDegrees(atan(angleTan))
        return when {
            //Quadrant 1
            (point2.x > point1.x && point2.y < point1.y) -> -angle.toFloat()
            //Quadrant 2
            (point2.x < point1.x && point2.y < point1.y) -> -180 + angle.toFloat()
            //Quadrant 3
            (point2.x < point1.x && point2.y > point1.y) -> 180 - angle.toFloat()
            //Quadrant 4
            else -> angle.toFloat()

        }
    }

    private fun findEdgeLength(point1: Point, point2: Point): Int {
        val triangleEdgeLength1 = abs(point1.x - point2.x).toDouble()
        val triangleEdgeLength2 = abs(point1.y - point2.y).toDouble()
        return sqrt(triangleEdgeLength1.pow(2) + triangleEdgeLength2.pow(2)).toInt()
    }
    //endregion


    fun clearPressedVertices() {
        val tempPair = _pressedVertices.value ?: Pair(null, null)
        _pressedVertices.value = Pair(null, null)
        tempPair.first?.setBackgroundResource(R.drawable.img_graph_vertex)
        tempPair.second?.setBackgroundResource(R.drawable.img_graph_vertex)
    }

    private fun setViewLayoutParams(view: View, width: Int, height: Int, x: Int, y: Int) {
        val params = FrameLayout.LayoutParams(
            width,
            height
        )
        params.setMargins(x, y, 0, 0)
        view.layoutParams = params
    }

    fun getNeighbour(
        firstVertexName: String,
        secondVertexName: String
    ): VertexNeighbour? {
        adjacencyList[firstVertexName]?.let {
            it.neighbours.forEach { neighbour ->
                if (neighbour.name == secondVertexName) {
                    return neighbour
                }
            }
        }
        return null
    }


    //region delete graph components
    fun deleteChosenEdge(): List<View>? {
        pressedVertices.value?.let {
            if (it.second != null && it.first != null) {
                val firstButton = it.first as AppCompatButton
                val secondButton = it.second as AppCompatButton
                val neighbour = getNeighbour(
                    secondButton.text.toString(),
                    firstButton.text.toString()
                )
                if (neighbour != null) {
                    adjacencyList[secondButton.text.toString()]?.neighbours?.remove(neighbour)
                    println(adjacencyList)
                    return listOf(
                        neighbour.edgeView,
                        neighbour.firstArrowPetalView,
                        neighbour.secondArrowPetalView,
                        neighbour.weightView
                    )
                }
            }
        }
        return null
    }

    fun deleteChosenVertex(): MutableList<View>? {

        pressedVertices.value?.let {
            if (it.first != null) {
                val vertexView = it.first as AppCompatButton
                val viewsList = mutableListOf<View>()
                adjacencyList[vertexView.text.toString()]?.neighbours?.forEach { neighbour ->
                    viewsList.add(neighbour.edgeView)
                    viewsList.add(neighbour.firstArrowPetalView)
                    viewsList.add(neighbour.secondArrowPetalView)
                    viewsList.add(neighbour.weightView)
                }
                viewsList.add(vertexView)
                adjacencyList.remove(vertexView.text.toString())
                println(adjacencyList)
                return viewsList
            }

        }
        return null
    }
    //endregion

    private fun initGraph() {
        val algorithmAdjacencyList = mutableMapOf<String, Neighbours>()
        adjacencyList.forEach {
            algorithmAdjacencyList[it.key] = mutableListOf()

            it.value.neighbours.forEach { vertexNeighbour ->
                algorithmAdjacencyList[it.key]?.add(
                    Pair(
                        vertexNeighbour.name,
                        vertexNeighbour.weightView.text.toString().toInt()
                    )
                )
            }

        }
        graph = Graph(algorithmAdjacencyList)
    }

    fun nextAlgorithmStep() {
        if(!bellmanFordAlgorithm.hasNext()) {
            changePathColor(
                highlightedPath,
                defaultVertexDrawable,
                defaultEdgeDrawable
            )
            return
        }
        val steps = bellmanFordAlgorithm.getSteps()
        _algorithmSteps.value = steps
        checkLastStep(steps.last())
    }

    fun previousAlgorithmStep() {
        bellmanFordAlgorithm.stepBack()
        bellmanFordAlgorithm.stepBack()
        nextAlgorithmStep()
    }

    fun toEndAlgorithmStep() {
        val steps = bellmanFordAlgorithm.getAllSteps()
        _algorithmSteps.value = steps
        checkLastStep(steps.last())
    }

    private fun checkLastStep(lastStep: Step) {
        if(lastStep.stepMsg == StepMsg.PATH) {
            changePathColor(
                highlightedPath,
                defaultVertexDrawable,
                defaultEdgeDrawable
            )
            highlightedPath = bellmanFordAlgorithm.getPath(
                lastStep.stepData.secondVertexParam
            )
            changePathColor(
                highlightedPath,
                highlightedVertexDrawable,
                highlightedEdgeDrawable
            )
        }
    }

    private fun changePathColor(path: List<String>, @DrawableRes vertexDrawable: Int, @DrawableRes edgeDrawable: Int) {
        for(i in 0..path.size - 2) {
            adjacencyList[path[i]]?.vertexView?.setBackgroundResource(vertexDrawable)
            val edge = getNeighbour(path[i], path[i+1])
            edge?.firstArrowPetalView?.setBackgroundResource(edgeDrawable)
            edge?.secondArrowPetalView?.setBackgroundResource(edgeDrawable)
            edge?.edgeView?.setBackgroundResource(edgeDrawable)
        }
        if(path.isNotEmpty())
            adjacencyList[path.last()]?.vertexView?.setBackgroundResource(vertexDrawable)
    }
}
