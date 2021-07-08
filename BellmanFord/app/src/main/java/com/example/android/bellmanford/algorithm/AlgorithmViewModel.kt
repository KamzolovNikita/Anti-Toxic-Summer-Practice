package com.example.android.bellmanford.algorithm

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
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

    private val _eventBackNavigate = MutableLiveData<Boolean>()
    val eventBackNavigate: LiveData<Boolean>
        get() = _eventBackNavigate

    fun onBackNavigate() {
        Log.d("ALGO_VIEW_MODEL", "onBackNavigate()")
        _eventBackNavigate.value = true
    }

    fun onBackNavigateFinish() {
        _eventBackNavigate.value = false
    }

    private val _eventAlgoInfoShow = MutableLiveData<Boolean>()
    val eventAlgoInfoShow: LiveData<Boolean>
        get() = _eventAlgoInfoShow

    fun onAlgoInfoShow() {
        Log.d("ALGO_VIEW_MODEL", "onAlgoInfoShow()")
        _eventAlgoInfoShow.value = true
    }

    fun onAlgoInfoShowFinish() {
        _eventAlgoInfoShow.value = false
    }

    private val _eventAlgoStepShow = MutableLiveData<Boolean>()
    val eventAlgoStepShow: LiveData<Boolean>
        get() = _eventAlgoStepShow

    fun onAlgoStepShow() {
        Log.d("ALGO_VIEW_MODEL", "onAlgoStepShow()")
        _eventAlgoStepShow.value = true
    }

    fun onAlgoStepShowFinish() {
        _eventAlgoStepShow.value = false
    }


    private val _pressedVertices = MutableLiveData<Pair<View?, View?>>()
    val pressedVertices: LiveData<Pair<View?, View?>>
        get() = _pressedVertices

    private val _eventVertexAlreadyExist = MutableLiveData<Boolean>()
    val eventVertexAlreadyExist: LiveData<Boolean>
        get() = _eventVertexAlreadyExist

    private var vertexDiameter = 0
    private var edgeLineHeight = 0
    private var edgeArrowHeight = 0
    private var edgeArrowWidth = 0

    private val adjacencyList = mutableMapOf<String, VertexInfo>()

    fun initDimensions(context: Context) {
        vertexDiameter =
            context.resources.getDimension(R.dimen.size_fragment_algorithm_vertex).toInt()
        edgeLineHeight =
            context.resources.getDimension(R.dimen.height_fragment_algorithm_edge).toInt()
        edgeArrowHeight =
            context.resources.getDimension(R.dimen.height_fragment_algorithm_arrow).toInt()
        edgeArrowWidth =
            context.resources.getDimension(R.dimen.width_fragment_algorithm_arrow).toInt()
    }

    fun onVertexAlreadyExistEvent() {
        _eventVertexAlreadyExist.value = true
    }

    fun onVertexAlreadyExistEventFinish() {
        _eventVertexAlreadyExist.value = false
    }


    //region vertex creating
    fun setupVertex(
        vertex: AppCompatButton,
        xClick: Int,
        yClick: Int,
        vertexName: String
    ): Boolean {
        setViewLayoutParams(
            vertex,
            vertexDiameter,
            vertexDiameter,
            xClick - vertexDiameter / 2,
            yClick - vertexDiameter / 2
        )
        vertex.isClickable = true
        vertex.setBackgroundResource(R.drawable.img_graph_vertex)

        vertex.text = vertexName.replace(" ", "").toUpperCase(Locale.ROOT).take(4)
        if (adjacencyList.containsKey(vertex.text)) {
            onVertexAlreadyExistEvent()
            return false
        }
        initVertexInAdjacencyList(vertex)

        vertex.setOnClickListener {
            val tempPair = _pressedVertices.value ?: Pair(null, null)
            when (vertex) {
                tempPair.first -> {
                    vertex.setBackgroundResource(R.drawable.img_graph_vertex)
                    _pressedVertices.value = Pair(tempPair.second, null)
                }
                tempPair.second -> {
                    vertex.setBackgroundResource(R.drawable.img_graph_vertex)
                    _pressedVertices.value = Pair(tempPair.first, null)
                }

                else -> {
                    vertex.setBackgroundResource(R.drawable.img_graph_vertex_selected)
                    tempPair.second?.setBackgroundResource(R.drawable.img_graph_vertex)
                    _pressedVertices.value = Pair(vertex, tempPair.first)

                }
            }
        }

        return true
    }

    private fun initVertexInAdjacencyList(vertex: AppCompatButton) {
        val params = vertex.layoutParams as FrameLayout.LayoutParams
        val center =
            Point(params.leftMargin + vertexDiameter / 2, params.topMargin + vertexDiameter / 2)
        adjacencyList[vertex.text.toString()] = VertexInfo(vertex, mutableListOf(), center)
    }
    //endregion

    //region edge creating
    fun setupEdge(
        edge: View,
        arrowPetal1: View,
        arrowPetal2: View,
        firstVertex: AppCompatButton,
        secondVertex: AppCompatButton
    ): Boolean {
        val vertexInfo1: VertexInfo = adjacencyList.getOrElse(firstVertex.text.toString(), {
            return false
        })
        val vertexInfo2: VertexInfo = adjacencyList.getOrElse(secondVertex.text.toString(), {
            return false
        })
        val point1 = vertexInfo1.position
        val point2 = vertexInfo2.position

        val length = findEdgeLength(point1, point2)
        val rotation = findEdgeRotation(point1, point2)

        initLine(edge, point1, length, rotation)
        initArrowPetal(arrowPetal1, point2, rotation, 30F)
        initArrowPetal(arrowPetal2, point2, rotation, -30F)

        val edgeSpawnState =
            checkEdgesSpawnState(
                vertexInfo1,
                vertexInfo2,
                firstVertex.text.toString(),
                secondVertex.text.toString()
            )

        if (edgeSpawnState == EdgeSpawnStates.ALREADY_EXIST) return false

        adjacencyList[firstVertex.text.toString()]?.neighbours?.add(
            VertexNeighbour(
                edge,
                secondVertex.text.toString()
            )
        )

        if (edgeSpawnState == EdgeSpawnStates.OPPOSITE_EXIST) {

            getOppositeEdge(firstVertex.text.toString(), vertexInfo2)?.let {
                moveEdgesToFit(
                    VertexNeighbour(edge, firstVertex.text.toString()),
                    it
                )
            }
        }

        return true
    }

    private fun moveEdgesToFit(firstEdge: VertexNeighbour, secondEdge: VertexNeighbour) {
        val point1 = calculateEdgesPosition(
                Point(firstEdge.edge.x.toInt(), firstEdge.edge.y.toInt()),
        firstEdge.edge.rotation.toDouble(),
        true
        )
        val point2 = calculateEdgesPosition(
            Point(firstEdge.edge.x.toInt(), firstEdge.edge.y.toInt()),
            firstEdge.edge.rotation.toDouble(),
            false
        )
        println("hello")
        var params = firstEdge.edge.layoutParams as FrameLayout.LayoutParams
        println(params.topMargin)
        println(params.leftMargin)
        println(vertexDiameter * 3 / 8)
        params.topMargin = params.topMargin + vertexDiameter * 3 / 8
        //params.setMargins(firstEdge.edge.marginLeft , params.topMargin + 3 / 4 * vertexDiameter / 2, 0, 0)
        println(firstEdge.edge)
        println(params.topMargin)
        println(params.leftMargin)
        firstEdge.edge.layoutParams = params
        params = secondEdge.edge.layoutParams as FrameLayout.LayoutParams
        params.topMargin = params.topMargin - vertexDiameter * 3 / 8
        params.setMargins(secondEdge.edge.marginLeft , secondEdge.edge.marginTop - 3 / 4 * vertexDiameter / 2, 0, 0)
        secondEdge.edge.layoutParams = params
    }

    private fun calculateEdgesPosition(point: Point, rotation: Double, toTop: Boolean): Point {
        val circumferentialOffsetX = 3 / 4 * vertexDiameter / 2 * cos(toRadians(rotation))
        val circumferentialOffsetY = 3 / 4 * vertexDiameter / 2 * sin(toRadians(rotation))
        return when (toTop) {
            false -> {
                Point(
                    (point.x - circumferentialOffsetX).toInt(),
                    (point.y - circumferentialOffsetY).toInt()
                )
            }
            true -> {
                Point(
                    (point.x + circumferentialOffsetX).toInt(),
                    (point.y + circumferentialOffsetY).toInt()
                )
            }
        }
    }

    private fun getOppositeEdge(
        oppositeVertexName: String,
        vertexInfo: VertexInfo
    ): VertexNeighbour? {
        vertexInfo.neighbours.forEach {
            if (it.name == oppositeVertexName)
                return it
        }
        return null
    }

    private fun checkEdgesSpawnState(
        vertexInfo1: VertexInfo,
        vertexInfo2: VertexInfo,
        firstVertexName: String,
        secondVertexName: String
    ): EdgeSpawnStates {
        vertexInfo1.neighbours.forEach {
            println(it.name)
            if (it.name == secondVertexName)
                return EdgeSpawnStates.ALREADY_EXIST
        }
        vertexInfo2.neighbours.forEach {
            if (it.name == firstVertexName)
                return EdgeSpawnStates.OPPOSITE_EXIST
        }
        return EdgeSpawnStates.NOTHING_EXIST
    }

    private fun initEdgesInAdjacencyMap() {

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

    private fun calculateArrowPosition(point: Point, rotation: Double): Point {
        val circumferentialOffsetX = vertexDiameter / 2 * cos(toRadians(rotation))
        val circumferentialOffsetY = vertexDiameter / 2 * sin(toRadians(rotation))

        return Point(
            (point.x - circumferentialOffsetX).toInt(),
            (point.y - circumferentialOffsetY).toInt()
        )
    }

    private fun initArrowPetal(
        arrowPetal: View,
        point: Point,
        rotation: Float,
        additionalRotation: Float
    ) {
        val pointWithDeviation =
            calculateArrowPosition(point, rotation.toDouble())
        setViewLayoutParams(
            arrowPetal,
            edgeArrowWidth,
            edgeArrowHeight,
            pointWithDeviation.x,
            pointWithDeviation.y
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
}
