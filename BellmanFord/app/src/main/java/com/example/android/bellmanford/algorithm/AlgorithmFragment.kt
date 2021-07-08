package com.example.android.bellmanford.algorithm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.bellmanford.R
import com.example.android.bellmanford.databinding.FragmentAlgorithmBinding
import com.example.android.bellmanford.dialogs.VertexNameDialogFragment
import com.example.android.bellmanford.dialogs.VertexNameEntered


class AlgorithmFragment : Fragment(), VertexNameEntered {

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

        viewModel.eventAlgoStepShow.observe(viewLifecycleOwner, { event ->
            if (event) {
                showAlgoStepPopUp(binding.btnAlgoStep)
                viewModel.onAlgoStepShowFinish()
            }
        })

        viewModel.eventAlgoInfoShow.observe(viewLifecycleOwner, { event ->
            if (event) {
                showAlgoInfoPopUp(binding.btnAlgoInfo)
                viewModel.onAlgoInfoShowFinish()
            }
        })




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

        viewModel.pressedVertices.observe(viewLifecycleOwner, {
            if (it.first != null && it.second != null) {
                val newLine = View(requireContext())
                val newArrow = View(requireContext())
                viewModel.setupEdge(
                    newLine, newArrow, it.first as AppCompatButton,
                    it.second as AppCompatButton
                )
                binding.fragmentAlgorithmFltCanvas.addView(newLine)
                binding.fragmentAlgorithmFltCanvas.addView(newArrow)
                viewModel.clearPressedVertices()
            }
        })


        return binding.root
    }

    override fun receiveName(name: String) {
        val newVertex = AppCompatButton(requireContext())
        if (name.isEmpty()) {
            vertexInitErrorToast(getString(R.string.toast_explanation_name_not_entered))
            return
        }
        val view = viewModel.setupVertex(newVertex, xClick, yClick, name)
        if (view == null) {
            vertexInitErrorToast(getString(R.string.toast_explanation_already_exist))
        } else {
            binding.fragmentAlgorithmFltCanvas.addView(view)
        }

    }

    private fun vertexInitErrorToast(explanation: String) {
        Toast.makeText(
            context,
            getString(R.string.toast_text_vertex_was_not_spawned, explanation),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showAlgoInfoPopUp(view: View) {

        val popupView: View = LayoutInflater.from(activity).inflate(R.layout.algoinfo_popup, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val exitButton = popupView.findViewById<ImageButton>(R.id.exit_button)

        exitButton.setOnClickListener{
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }

        popupWindow.animationStyle = R.style.PopUpAnimationFromBottom
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
    }

    private fun showAlgoStepPopUp(view: View) {

        val popupView: View = LayoutInflater.from(activity).inflate(R.layout.algostep_popup, null)


        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )

        val exitButton = popupView.findViewById<ImageButton>(R.id.exit_button)

        exitButton.setOnClickListener{
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }

        popupWindow.animationStyle = R.style.PopUpAnimationFromLeft
        popupWindow.showAtLocation(view, Gravity.LEFT, 0, 0)
    }
}

