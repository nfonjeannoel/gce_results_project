package com.camgist.gceresults.verification

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.camgist.gceresults.R
import com.camgist.gceresults.databinding.FragmentVerificationBinding
import com.camgist.gceresults.home.tabs.disableButton
import com.camgist.gceresults.home.tabs.enableButton
import com.camgist.gceresults.selectLevelWithMetadata
import com.camgist.gceresults.showErrorFragment
import com.camgist.gceresults.network.MetadataService
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class VerificationFragment : Fragment() {

    private lateinit var binding: FragmentVerificationBinding
    private lateinit var adapterLevels: ArrayAdapter<String>
    private lateinit var adapterYears: ArrayAdapter<String>
    private lateinit var metadataService: MetadataService
    private val viewModel: VerificationViewModel by lazy {
        ViewModelProvider(this).get(VerificationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verification, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize metadata service
        metadataService = MetadataService(requireContext())
        
        // Initialize adapters with empty lists
        adapterLevels = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        
        adapterYears = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            mutableListOf<String>()
        )
        
        // Set adapters
        binding.spOpt1Level.setAdapter(adapterLevels)
        binding.actvOp1Year.setAdapter(adapterYears)
        
        // Load metadata
        loadMetadata()

        binding.btnFetchResult.setOnClickListener {
            if (
                binding.spOpt1Level.text.toString().trim().isNullOrEmpty() ||
                binding.actvOp1Year.text.toString().trim().isNullOrEmpty()
            ) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                disableButton(it, requireContext(), binding.ivLoading, binding.ll1)

                val filters = hashMapOf(
                    "opt" to "6",
                    "level" to selectLevelWithMetadata(requireContext(), binding.spOpt1Level.text.toString()),
                    "year" to binding.actvOp1Year.text.toString(),
                )
                viewModel.getResultFromApi(filters)
            }
        }

        // Observe loading state
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (!isLoading) {
                enableButton(binding.btnFetchResult, binding.ivLoading, binding.ll1)
            }
        })

        viewModel.response.observe(viewLifecycleOwner, Observer {
            if (it != null){
                when(it){
                    1 -> {
                        showErrorFragment(layoutInflater, requireContext(), R.layout.result_released_dialog)
                    }
                    -1 -> {
                        showErrorFragment(layoutInflater, requireContext(), R.layout.not_released_dialog)
                    }
                    else -> {
                        Toast.makeText(context, "Unexpected response. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }
                viewModel.doneShowingResponse()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()){
                // Show a more informative error message
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                viewModel.doneShowingError()
            }
        })
    }
    
    private fun loadMetadata() {
        lifecycleScope.launch {
            try {
                // Load levels
                val levels = metadataService.getLevels()
                adapterLevels.clear()
                adapterLevels.addAll(levels)
                adapterLevels.notifyDataSetChanged()
                
                // Load years
                val years = metadataService.getYears()
                adapterYears.clear()
                adapterYears.addAll(years)
                adapterYears.notifyDataSetChanged()
                
            } catch (e: Exception) {
                // Handle error silently - fallback data will be used
            }
        }
    }
}