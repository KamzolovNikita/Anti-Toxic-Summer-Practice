package com.example.android.bellmanford.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.bellmanford.R
import com.example.android.bellmanford.databinding.FragmentAlgorithmInfoBinding

class AlgorithmInfoFragment : Fragment() {

    lateinit var binding: FragmentAlgorithmInfoBinding
    lateinit var viewModel: AlgorithmInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_algorithm_info, container, false
        )

        viewModel = ViewModelProvider(this).get(AlgorithmInfoViewModel::class.java)

        viewModel.eventBackNavigate.observe(viewLifecycleOwner, { event ->
            if(event) {
                findNavController().popBackStack()
                viewModel.onBackNavigateFinish()
            }
        })

        binding.infoViewModel = viewModel

        return binding.root
    }

}