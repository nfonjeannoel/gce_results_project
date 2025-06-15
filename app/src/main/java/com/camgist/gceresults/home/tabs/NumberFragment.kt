package com.camgist.gceresults.home.tabs

import android.app.Application
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.camgist.gceresults.*
import com.camgist.gceresults.database.StudentResultDao
import com.camgist.gceresults.database.StudentResultDatabase
import com.camgist.gceresults.databinding.FragmentNumberBinding
import com.camgist.gceresults.home.HomeFragmentDirections
import com.camgist.gceresults.home.HomeViewModel
import com.camgist.gceresults.home.HomeViewModelFactory
import com.camgist.gceresults.network.MetadataService
import com.camgist.gceresults.utils.NetworkUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.launch


class NumberFragment : Fragment() {

    private lateinit var binding : FragmentNumberBinding
    private lateinit var application: Application
    private lateinit var database : StudentResultDao
    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var adapterLevels: ArrayAdapter<String>
    private lateinit var adapterYears: ArrayAdapter<String>
    private lateinit var metadataService: MetadataService
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var ivLoadingImage: ImageView
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_number, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"ca-app-pub-5796541899764838/1649575743", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
//                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                Log.d(TAG, 'Ad was loaded.')
                mInterstitialAd = interstitialAd
            }
        })

        application = requireNotNull(activity).application
        database = StudentResultDatabase.getInstance(application).studentResultDao
        viewModelFactory = HomeViewModelFactory(database)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)

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

        ivLoadingImage = binding.ivLoading
        
        // Load metadata
        loadMetadata()

        viewModel.results.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                //navigate to list result fragment
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToResultListFragment(it.toTypedArray())
                )
                viewModel.doneNavigatingListResult()
                enableButton(binding.btnOp2FetchResults, ivLoadingImage, binding.llCenterResult)
            }})

        viewModel.response.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                showErrorFragment(layoutInflater, requireContext(), R.layout.bottom_sheet_dialog)
                viewModel.doneShowingError()
                enableButton(binding.btnOp2FetchResults, ivLoadingImage, binding.llCenterResult)
            }
        })

        viewModel.noMatch.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                showNoMatchFragment(layoutInflater, requireContext(), R.layout.no_match_dialog)
                viewModel.doneShowingNoMatch()
                enableButton(binding.btnOp2FetchResults, ivLoadingImage, binding.llCenterResult)
            }
        })

        viewModel.showToast.observe(viewLifecycleOwner, Observer { showToast ->
            if (showToast){
                showToast("please do not leave this screen while loading", requireContext())
                viewModel.showingToastComplete()
            }
        })

        binding.btnOp2FetchResults.setOnClickListener {
            if (
                binding.spOp2Level.text.toString().trim().isNullOrEmpty() ||
                binding.actvOp2Year.text.toString().trim().isNullOrEmpty() ||
                binding.etOpt2CenterNumber.editText?.text.toString().trim().isNullOrEmpty()
            ) {
                //show error message
                showEmptyFieldToast(requireContext())
            } else {
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                disableButton(it, requireContext(), ivLoadingImage, binding.llCenterResult)

                // Check network connectivity before making API call
                if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                    // Show no internet dialog instead of making API call
                    showNoInternetFragment(layoutInflater, requireContext(), R.layout.no_internet_dialog)
                    enableButton(binding.btnOp2FetchResults, ivLoadingImage, binding.llCenterResult)
                    return@setOnClickListener
                }

                val filters = hashMapOf(
                    "opt" to "2",
                    "level" to selectLevelWithMetadata(requireContext(), binding.spOp2Level.text.toString()),
                    "year" to binding.actvOp2Year.text.toString().trim(),
                    "center_number" to binding.etOpt2CenterNumber.editText?.text.toString().trim()
                )
                viewModel.getResultFromApi(filters, false)
            }
        }
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
    
    override fun onResume() {
        super.onResume()
        setAdapters()
    }

    private fun setAdapters() {
        binding.spOp2Level.setAdapter(adapterLevels)
        binding.actvOp2Year.setAdapter(adapterYears)
    }
}