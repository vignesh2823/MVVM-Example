package com.example.mvvmtask.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exampleproject.responsemodel.TrendingResponseItem
import com.example.mvvmtask.Adapter.HomeAdapter
import com.example.mvvmtask.Network.ApiHelper
import com.example.mvvmtask.Network.RetrofitBuilder
import com.example.mvvmtask.Util.Status
import com.example.mvvmtask.Util.ViewModelFactory
import com.example.mvvmtask.ViewModel.HomeViewModel
import com.example.myapplication.R
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var viewModel: HomeViewModel
    lateinit var adapter: HomeAdapter

    private val TAG = "ToolbarFragment"
    private val STANDARD_APPBAR = 0
    private val SEARCH_APPBAR = 1
    private var mAppBarState: Int = 0
    lateinit var viewContactsBar: AppBarLayout
    lateinit var searchBar: AppBarLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewContactsBar = view.findViewById(R.id.viewContactsToolbar) as AppBarLayout
        searchBar = view.findViewById(R.id.searchToolbar) as AppBarLayout

        setAppBaeState(STANDARD_APPBAR);

        val ivSearchContact = view.findViewById(R.id.ivSearchIcon) as ImageView
        val ivBackArrow = view.findViewById(R.id.ivBackArrow) as ImageView
        val ivClose = view.findViewById(R.id.ivClose) as ImageView
        val etSearch = view.findViewById(R.id.etSearch) as EditText

        ivSearchContact.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "onClick: clicked searched icon")
                toggleToolBarState()
                etSearch.requestFocus()
            }
        })

        ivBackArrow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "onClick: clicked back icon")
                toggleToolBarState()
                etSearch.clearFocus()
            }
        })

        ivClose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d(TAG, "onClick: clicked back icon")
                toggleToolBarState()
                etSearch.text = null
                etSearch.clearFocus()
            }
        })
        etSearch.addTextChangedListener {
            adapter.filter.filter(etSearch.text.trim())
        }

        setupViewModel()
        swipe.setOnRefreshListener {
            swipe.setColorSchemeColors(Color.WHITE)
            swipe.setProgressBackgroundColorSchemeColor(Color.rgb(2, 101, 213))
            setupObservers()
        }
        try_again.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            setupObservers()
        }

        if (isNetworkAvailable()) {
            setupObservers()
        } else {
            viewModel.getLocalData(requireContext())!!.observe(requireActivity(), Observer {
                it?.let { list: List<TrendingResponseItem> ->
                    if (list.size > 0) {
                        retrieveLocalList(list)
                        list_view.visibility = View.VISIBLE
                        progressbar.visibility = View.GONE
                        no_internet.visibility = View.GONE
                    } else {
                        list_view.visibility = View.GONE
                        progressbar.visibility = View.GONE
                        no_internet.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(HomeViewModel::class.java)
    }

    private fun setupObservers() {
        viewModel.getList().observe(requireActivity(), Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        list_view.visibility = View.VISIBLE
                        progressbar.visibility = View.GONE
                        no_internet.visibility = View.GONE
                        resource.data?.let { users -> retrieveList(users) }
                    }
                    Status.ERROR -> {
                        swipe.isRefreshing = false
                        list_view.visibility = View.GONE
                        progressbar.visibility = View.GONE
                        no_internet.visibility = View.VISIBLE
                    }
                    Status.LOADING -> {
                        list_view.visibility = View.GONE
                        no_internet.visibility = View.GONE
                        if (swipe.visibility == View.VISIBLE) {
                        } else {
                            progressbar.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })

    }

    private fun retrieveList(users: List<TrendingResponseItem>) {
        setHasOptionsMenu(true)
        viewModel.insertData(requireContext(), users)
        swipe.isRefreshing = false
        list_view.layoutManager = LinearLayoutManager(requireContext())
        adapter = HomeAdapter(requireContext(), users, users) { users ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                    users
                )
            findNavController().navigate(action)
        }
        val layoutManager = LinearLayoutManager(context)
        list_view.layoutManager = layoutManager
        list_view.setHasFixedSize(true)
        list_view.adapter = adapter
    }

    private fun retrieveLocalList(users: List<TrendingResponseItem>) {
        setHasOptionsMenu(true)
        swipe.isRefreshing = false
        list_view.layoutManager = LinearLayoutManager(requireContext())
        adapter = HomeAdapter(requireContext(), users, users) { trendingResponseItem ->
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(trendingResponseItem)
            findNavController().navigate(action)

        }
        list_view.addItemDecoration(
            DividerItemDecoration(
                list_view.context,
                (list_view.layoutManager as LinearLayoutManager).orientation
            )
        )
        list_view.adapter = adapter
    }

    /*
        override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater.inflate(R.menu.menu_search, menu)
            super.onCreateOptionsMenu(menu, inflater)

            val searchItem = menu.findItem(R.id.action_search)
            val searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        adapter.filter.filter(newText)
                    }
                    return true
                }
            })
        }
    */

    @SuppressLint("ServiceCast")
    private fun setAppBaeState(state: Int) {
        Log.d(TAG, "setAppBaeState: changing app bar state to: " + state)
        mAppBarState = state
        if (mAppBarState === STANDARD_APPBAR) {
            searchBar.setVisibility(View.GONE)
            viewContactsBar.setVisibility(View.VISIBLE)
            val view = getView()
            val im =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            try {
                im.hideSoftInputFromWindow(view?.getWindowToken(), 0) // make keyboard hide
            } catch (e: NullPointerException) {
                Log.d(TAG, "setAppBaeState: NullPointerException: " + e)
            }
        } else if (mAppBarState === SEARCH_APPBAR) {
            viewContactsBar.setVisibility(View.GONE)
            searchBar.setVisibility(View.VISIBLE)
            val im =
                getActivity()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0) // make keyboard popup
        }
    }

    private fun toggleToolBarState() {
        Log.d(TAG, "toggleToolBarState: toggling AppBarState.")
        if (mAppBarState === STANDARD_APPBAR) {
            setAppBaeState(SEARCH_APPBAR)
        } else {
            setAppBaeState(STANDARD_APPBAR)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }
}