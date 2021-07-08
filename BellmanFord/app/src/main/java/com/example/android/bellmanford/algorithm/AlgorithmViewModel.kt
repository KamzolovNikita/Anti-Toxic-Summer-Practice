package com.example.android.bellmanford.algorithm

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.bellmanford.R
import java.lang.Math.*
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt


class AlgorithmViewModel : ViewModel() {

    private val _pressedVertices = MutableLiveData<Pair<View?, View?>>()
    val pressedVertices: LiveData<Pair<View?, View?>>
        get() = _pressedVertices

    private var vertexDiameter = -1

    private val adjacencyList = mutableMapOf<String, VertexInfo>()


    fun setupVertex(vertex: AppCompatButton, xClick: Int, yClick: Int, vertexName: String): View? {
        initVertexDiameter(vertex.context)
        setViewLayoutParams(vertex, vertexDiameter, vertexDiameter, xClick, yClick)
        vertex.isClickable = true
        vertex.setBackgroundResource(R.drawable.img_graph_vertex)

        vertex.text = vertexName.replace(" ", "").take(4)
        if (adjacencyList.containsKey(vertex.text)) {
            return null
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

        return vertex
    }


    fun setupEdge(
        edge: View,
        arrow: View,
        firstVertex: AppCompatButton,
        secondVertex: AppCompatButton
    ) {
        val point1 = adjacencyList[firstVertex.text.toString()]!!.position
        val point2 = adjacencyList[secondVertex.text.toString()]!!.position
        val length = findEdgeLength(point1, point2)
        val rotation = findEdgeRotation(point1, point2)

        initLine(edge, point1, length, rotation)
        initArrow(arrow, point2, rotation)

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
        edge.pivotY = 0F
        edge.rotation = rotation
        edge.setBackgroundResource(R.drawable.view_line)
    }

    private fun calculateArrowPosition(point: Point, rotation: Double, context: Context) : Point {
        val arrowHeight = context.resources.getDimension(R.dimen.height_fragment_algorithm_arrow)
        val arrowWidth = context.resources.getDimension(R.dimen.width_fragment_algorithm_arrow)

        val lineHeight = context.resources.getDimension(R.dimen.height_fragment_algorithm_edge)

        val circumferentialOffsetX = vertexDiameter / 2 * cos(toRadians(rotation))
        val circumferentialOffsetY = vertexDiameter / 2 * sin(toRadians(rotation))

        val offsetForCenteringArrowViewX = (arrowWidth / 2 + lineHeight / 2) * sin(toRadians(rotation))
        val offsetForCenteringArrowViewY = (arrowHeight + lineHeight) / 2 * cos(toRadians(rotation))

        return when {
            rotation >= 90 -> {
                Point(
                    (point.x - circumferentialOffsetX - offsetForCenteringArrowViewX).toInt(),
                    (point.y - circumferentialOffsetY + offsetForCenteringArrowViewY).toInt()
                )
            }
            rotation >= 0 -> {
                Point(
                    (point.x - circumferentialOffsetX - circumferentialOffsetX).toInt(),
                    (point.y - circumferentialOffsetY - circumferentialOffsetY).toInt()
                )
            }
            else -> {
                Point(point.x, point.y)
            }
        }
    }

    private fun initArrow(arrow: View, point: Point, rotation: Float) {
        val pointWithDeviation = calculateArrowPosition(point, rotation.toDouble(), arrow.context)
        setViewLayoutParams(
            arrow,
            arrow.context.resources.getDimension(R.dimen.width_fragment_algorithm_arrow).toInt(),
            arrow.context.resources.getDimension(R.dimen.height_fragment_algorithm_arrow).toInt(),
            pointWithDeviation.x,
            pointWithDeviation.y
        )
        println(cos(toRadians(rotation.toDouble())))
        println(sin(toRadians(rotation.toDouble())))
        arrow.isClickable = true
        arrow.rotation = rotation
        arrow.setBackgroundResource(R.drawable.img_edge_arrow)
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

    private fun initVertexInAdjacencyList(vertex: AppCompatButton) {
        val params = vertex.layoutParams as FrameLayout.LayoutParams
        val center =
            Point(params.leftMargin + vertexDiameter / 2, params.topMargin + vertexDiameter / 2)
        adjacencyList[vertex.text.toString()] = VertexInfo(vertex, null, center)
    }

    fun clearPressedVertices() {
        val tempPair = _pressedVertices.value ?: Pair(null, null)
        _pressedVertices.value = Pair(null, null)
        tempPair.first?.setBackgroundResource(R.drawable.img_graph_vertex)
        tempPair.second?.setBackgroundResource(R.drawable.img_graph_vertex)
    }


    private fun initVertexDiameter(context: Context) {
        if (vertexDiameter == -1) {
            vertexDiameter =
                context.resources.getDimension(R.dimen.size_fragment_algorithm_vertex).toInt()
        }
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
