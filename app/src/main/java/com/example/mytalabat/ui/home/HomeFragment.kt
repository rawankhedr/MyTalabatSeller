package com.example.mytalabat.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mytalabat.data.remote.FirebaseDataSource
import com.example.mytalabat.data.repository.AuthRepository
import com.example.mytalabat.data.repository.UserRepository
import com.example.mytalabat.databinding.FragmentHomeBinding
import com.example.mytalabat.util.Resource

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        val dataSource = FirebaseDataSource()
        HomeViewModelFactory(
            AuthRepository(dataSource),
            UserRepository(dataSource)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { resource ->
            Log.d("HomeFragment", "Resource state: ${resource::class.simpleName}")
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvWelcome.text = "Loading..."
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    resource.data?.let { profile ->
                        Log.d("HomeFragment", "Profile loaded: ${profile.name}")
                        binding.tvWelcome.text = "Welcome, ${profile.name}!"
                        binding.tvEmail.text = profile.email
                    } ?: run {
                        binding.tvWelcome.text = "Welcome!"
                        binding.tvEmail.text = "No profile data"
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvWelcome.text = "Welcome!"
                    binding.tvEmail.text = "Error loading profile"
                    Log.e("HomeFragment", "Error: ${resource.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}