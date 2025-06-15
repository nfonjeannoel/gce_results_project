package com.camgist.gceresults.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.camgist.gceresults.R
import com.camgist.gceresults.databinding.FragmentHomeBinding
import com.camgist.gceresults.home.tabs.*
import com.camgist.gceresults.home.tabs.adapters.TabsSetup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var tabList: ArrayList<Fragment>

    private var backPressed: Boolean = false
    private lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (backPressed) {
                        requireActivity().finishAffinity()
                    } else {
                        backPressed = true
                        Toast.makeText(context, "Press back again to exit", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MobileAds.initialize(requireContext()) {}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)


        val tabsTitle = arrayOf(
            "NAME",
            "Center number",
            "Center name",
        )
        tabList = arrayListOf(
            NameFragment(), NumberFragment(), CenterFragment()
        )

        val viewPagerAdapter = TabsSetup(tabList, requireActivity() as AppCompatActivity)
        binding.viewpager.adapter = viewPagerAdapter

        TabLayoutMediator(
            binding.tabs, binding.viewpager
        ) { tab, position -> tab.text = tabsTitle[position] }.attach()



        setHasOptionsMenu(true)
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
//            R.id.mi_help -> {
//                removeHelp(requireActivity().findViewById(R.id.tv_help))
//            }
            R.id.tab7Fragment -> {
                setCurrentFragment(0)
                return true
            }
            R.id.tab2Fragment -> {
                setCurrentFragment(1)
                return true
            }
            R.id.tab8Fragment -> {
                setCurrentFragment(2)
                return true
            }


        }
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun setCurrentFragment(i: Int) {
        binding.viewpager.currentItem = i
    }

}