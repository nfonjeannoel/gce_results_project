package com.camgist.gceresults.resultlist

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.camgist.gceresults.R
import com.camgist.gceresults.databinding.FragmentResultListBinding
import com.camgist.gceresults.resultlist.details.algorithms.calculatePoint
import com.camgist.gceresults.resultlist.details.algorithms.getRecords
import com.camgist.gceresults.network.ResultData
import com.camgist.gceresults.resultlist.adapter.ResultClickListener
import com.camgist.gceresults.resultlist.adapter.ResultListAdapter

class ResultListFragment : Fragment() {
    //    private lateinit var viewModel: ResultListVewModel
//    private lateinit var viewModelFactory: ResultListViewModelFactory
    private val sharedViewModel: SharedViewModel by lazy {
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }
    private lateinit var args: ResultListFragmentArgs
    private lateinit var adapter: ResultListAdapter

    //    private lateinit var matchedPeople: MutableList<ResultData>
    private lateinit var results: List<ResultData>

    private lateinit var binding: FragmentResultListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result_list, container, false)
        adapter = ResultListAdapter(
            ResultClickListener {
                findNavController().navigate(
                    ResultListFragmentDirections.actionResultListFragmentToDetailsFragment(it)
                )
            }
        )

        binding.rvListResults.adapter = adapter
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ResultListFragmentArgs.fromBundle(requireArguments())
        results = args.resultList.toList()
        sharedViewModel.setResults(results)

        sharedViewModel.resultsToDisplay.observe(viewLifecycleOwner, Observer {
            adapter.results = it
            binding.rvListResults.adapter?.notifyDataSetChanged()

        })

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.results_menu, menu)

        val searchItem = menu.findItem(R.id.mi_search)
        val searchView = searchItem.actionView as SearchView



        if (sharedViewModel.searchText.value?.isNotEmpty() == true) {
            searchView.onActionViewExpanded()
            searchView.setQuery(sharedViewModel.searchText.value, true)
        } else {
            searchView.onActionViewCollapsed()
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return true
            }
        })
    }

    private fun search(text: String?) {
        text?.let {
            sharedViewModel.search(text)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_sort_papers -> {
                sortPapers()
            }

            R.id.mi_clear_filters -> {
                clearFilter()
            }

            R.id.mi_sort_points -> sortPoints()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortPapers() {
        sharedViewModel.sortPapers()
        scrollToTop()
    }

    private fun sortPoints() {
        sharedViewModel.sortPoints()
        scrollToTop()
    }

    private fun scrollToTop() {
        binding.rvListResults.scrollToPosition(0)
    }

    private fun clearFilter() {
        sharedViewModel.clearFilter()
    }

//    override fun onDestroy() {
//        // what viewmodelstore does this clear?
//        // check this if there are feature issues of viewmodels being cleared
//        super.onDestroy()
//        activity?.viewModelStore?.clear()
//    }

    companion object {
        const val TAG = "ResultListFragment"
    }


}