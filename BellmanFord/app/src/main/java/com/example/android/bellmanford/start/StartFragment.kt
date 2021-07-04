package com.example.android.bellmanford.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.bellmanford.R
import com.example.android.bellmanford.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    lateinit var binding: FragmentStartBinding
    lateinit var viewModel: StartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_start, container, false
        )

        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)

        binding.startViewModel = viewModel

        viewModel.eventInfoNavigate.observe(viewLifecycleOwner, { event ->
            if (event) {
                findNavController().navigate(R.id.action_startFragment_to_algorithmInfoFragment)
                viewModel.onInfoNavigateFinish()
            }
        })

        viewModel.eventDevelopersNavigate.observe(viewLifecycleOwner, { event ->
            if (event) {
                findNavController().navigate(R.id.action_startFragment_to_developersFragment)
                viewModel.onDevelopersNavigateFinish()
            }
        })


        return binding.root
    }

}