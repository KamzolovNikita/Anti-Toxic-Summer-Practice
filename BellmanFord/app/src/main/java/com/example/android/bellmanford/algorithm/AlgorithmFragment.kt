package com.example.android.bellmanford.algorithm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
                viewModel.setupEdge(newLine, newArrow, it.first as AppCompatButton,
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
}

