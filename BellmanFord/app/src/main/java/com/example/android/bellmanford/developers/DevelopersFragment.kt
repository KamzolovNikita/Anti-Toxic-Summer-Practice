package com.example.android.bellmanford.developers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.bellmanford.R
import com.example.android.bellmanford.databinding.FragmentDevelopersBinding

class DevelopersFragment : Fragment() {

    lateinit var binding: FragmentDevelopersBinding
    lateinit var viewModel: DevelopersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_developers, container, false
        )

        viewModel = ViewModelProvider(this).get(DevelopersViewModel::class.java)

        viewModel.eventBackNavigate.observe(viewLifecycleOwner, { event ->
            if(event) {
                findNavController().popBackStack()
                viewModel.onBackNavigateFinish()
            }
        })

        binding.developersViewModel = viewModel

        return binding.root
    }
}