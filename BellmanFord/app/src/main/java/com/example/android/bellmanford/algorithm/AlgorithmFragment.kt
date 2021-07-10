package com.example.android.bellmanford.algorithm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.bellmanford.R
import com.example.android.bellmanford.databinding.FragmentAlgorithmBinding
import com.example.android.bellmanford.dialogs.EdgeWeightDialogFragment
import com.example.android.bellmanford.dialogs.EdgeWeightEntered
import com.example.android.bellmanford.dialogs.VertexNameDialogFragment
import com.example.android.bellmanford.dialogs.VertexNameEntered
import com.example.android.bellmanford.util.AppFullscreen


class AlgorithmFragment : Fragment(), VertexNameEntered, EdgeWeightEntered {

    private lateinit var binding: FragmentAlgorithmBinding
    private lateinit var viewModel: AlgorithmViewModel

    private var xClick = 0
    private var yClick = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_algorithm, container, false
        )

        viewModel = ViewModelProvider(this).get(AlgorithmViewModel::class.java)
        binding.algorithmViewModel = viewModel

        viewModel.eventBackNavigate.observe(viewLifecycleOwner, { event ->
            if (event) {
                findNavController().popBackStack()
                viewModel.onBackNavigateFinish()
            }
        })

        viewModel.eventAlgorithmStepShow.observe(viewLifecycleOwner, { event ->
            if (event) {
                showAlgorithmStepPopUp(binding.btnAlgoStep)
                viewModel.onAlgorithmStepShowFinish()
            }
        })

        viewModel.eventAlgorithmInfoShow.observe(viewLifecycleOwner, { event ->
            if (event) {
                showAlgorithmInfoPopUp(binding.btnAlgoInfo)
                viewModel.onAlgorithmInfoShowFinish()
            }
        })



        viewModel.initDimensions(requireContext())

        binding.fragmentAlgorithmFltCanvas.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                xClick = event.x.toInt()
                yClick = event.y.toInt()
            }
            false
        }

        binding.fragmentAlgorithmFltCanvas.setOnClickListener {
            val vertexNameDialogFragment = VertexNameDialogFragment(this)
            activity?.let {
                vertexNameDialogFragment.show(it.supportFragmentManager, "New vertex")
            }

        }

        viewModel.eventVertexAlreadyExist.observe(viewLifecycleOwner, {
            if (it) {
                vertexInitErrorToast(getString(R.string.toast_explanation_already_exist))
                viewModel.onVertexAlreadyExistEventFinish()
            }
        })


        viewModel.pressedVertices.observe(viewLifecycleOwner, {
            if (it.first != null && it.second != null) {
                binding.fragmentAlgorithmImgBtnDeleteVertex.visibility = View.INVISIBLE
                val firstButton = it.first as AppCompatButton
                val secondButton = it.second as AppCompatButton
                val isEdgeAlreadyExist = viewModel.getNeighbourAlreadyExist(
                    secondButton.text.toString(), firstButton.text.toString()
                )
                if (isEdgeAlreadyExist != null) {
                    binding.fragmentAlgorithmImgBtnDeleteEdge.visibility = View.VISIBLE
                }
                else {
                    val edgeWeightDialogFragment = EdgeWeightDialogFragment(this)
                    activity?.let { temp ->
                        edgeWeightDialogFragment.show(temp.supportFragmentManager, "New edge")
                    }
                }
            }
            else if(it.first != null) {
                binding.fragmentAlgorithmImgBtnDeleteVertex.visibility = View.VISIBLE
                binding.fragmentAlgorithmImgBtnDeleteEdge.visibility = View.INVISIBLE
            }
            else {
                binding.fragmentAlgorithmImgBtnDeleteVertex.visibility = View.INVISIBLE
                binding.fragmentAlgorithmImgBtnDeleteEdge.visibility = View.INVISIBLE
            }
        })

        binding.fragmentAlgorithmImgBtnDeleteVertex.setOnClickListener {
            val viewsToDelete = viewModel.deleteChosenVertex()
            viewsToDelete?.let {
                it.forEach { view ->
                    deleteViews(view)
                }
            }
            viewModel.clearPressedVertices()
        }

        binding.fragmentAlgorithmImgBtnDeleteEdge.setOnClickListener {
            val viewsToDelete = viewModel.deleteChosenEdge()
            viewsToDelete?.let {
                it.forEach { view ->
                    deleteViews(view)
                }
            }
            viewModel.clearPressedVertices()
        }


        return binding.root
    }

    private fun deleteViews(view: View) {
        binding.fragmentAlgorithmFltCanvas.removeView(view)
    }

    override fun receiveName(name: String) {
        val newVertex = AppCompatButton(requireContext())
        if (name.isEmpty()) {
            vertexInitErrorToast(getString(R.string.toast_explanation_name_not_entered))
            return
        }
        if (viewModel.setupVertex(newVertex, xClick, yClick, name)) {
            binding.fragmentAlgorithmFltCanvas.addView(newVertex)
        }


    }

    override fun receiveWeight(weight: String) {
        val newLine = View(requireContext())
        val firstArrowPetal = View(requireContext())
        val secondArrowPetal = View(requireContext())
        val edgeWeight = TextView(requireContext())
        edgeWeight.text = weight
        viewModel.setupEdge(
            newLine,
            firstArrowPetal,
            secondArrowPetal,
            viewModel.pressedVertices.value?.second as AppCompatButton,
            viewModel.pressedVertices.value?.first as AppCompatButton,
            edgeWeight
        )

        edgeWeight.setTextColor(ContextCompat.getColor(requireContext(), R.color.main_blue))


        binding.fragmentAlgorithmFltCanvas.addView(newLine)
        binding.fragmentAlgorithmFltCanvas.addView(firstArrowPetal)
        binding.fragmentAlgorithmFltCanvas.addView(secondArrowPetal)
        binding.fragmentAlgorithmFltCanvas.addView(edgeWeight)
        edgeWeight.bringToFront()

        viewModel.clearPressedVertices()
    }

    private fun vertexInitErrorToast(explanation: String) {
        Toast.makeText(
            context,
            getString(R.string.toast_text_vertex_was_not_spawned, explanation),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAlgorithmInfoPopUp(view: View) {

        val popupView: View = LayoutInflater.from(activity).inflate(R.layout.algoinfo_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val exitButton = popupView.findViewById<ImageButton>(R.id.exit_button)

        exitButton.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }

        popupWindow.animationStyle = R.style.PopUpAnimationFromBottom
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            AppFullscreen.turnFullscreen(requireActivity())
        }
    }

    private fun showAlgorithmStepPopUp(view: View) {

        val popupView: View = LayoutInflater.from(activity).inflate(R.layout.algostep_popup, null)


        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )

        val exitButton = popupView.findViewById<ImageButton>(R.id.exit_button)

        exitButton.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }

        popupWindow.animationStyle = R.style.PopUpAnimationFromLeft
        popupWindow.showAtLocation(view, Gravity.START, 0, 0)

        popupWindow.setOnDismissListener {
            AppFullscreen.turnFullscreen(requireActivity())
        }
    }


}

