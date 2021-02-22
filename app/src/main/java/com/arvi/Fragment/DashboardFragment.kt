package com.arvi.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

import com.arvi.R
import com.google.android.material.tabs.TabLayout

class DashboardFragment : Fragment() {

    var tlDashboardData: TabLayout? = null
    var vpDashboardData:ViewPager?=null
    var appContext: Context? = null
    var snackbarView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        setIds(view)
        setTabLayoutData()
        return view
    }

    private fun setTabLayoutData() {
        try {
            tlDashboardData!!.addTab(tlDashboardData!!.newTab().setText("Summary"))
            tlDashboardData!!.addTab(tlDashboardData!!.newTab().setText("All Data"))
            tlDashboardData!!.setTabGravity(TabLayout.GRAVITY_FILL)

            val adapter =
                DashboardTabAdapter(appContext!!, childFragmentManager, tlDashboardData!!.getTabCount())
            vpDashboardData!!.setAdapter(adapter)

            vpDashboardData!!.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    tlDashboardData
                )
            )
            
            tlDashboardData!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    vpDashboardData!!.setCurrentItem(tab.position)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    internal inner class DashboardTabAdapter(
        private val myContext: Context,
        fm: FragmentManager,
        var totalTabs: Int
    ) :
        FragmentPagerAdapter(fm) {

        // this is for fragment tabs
        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {

                    return Dashboard_SummaryDataFragment()
                }
                1 -> {

                    return Dashboard_AllDataFragment()
                }
                else -> return Dashboard_SummaryDataFragment()
            }
        }

        // this counts total number of tabs
        override fun getCount(): Int {
            return totalTabs
        }
    }


    private fun setIds(view: View) {
        appContext = activity
        snackbarView = activity?.findViewById(android.R.id.content)
        tlDashboardData = view.findViewById(R.id.tlDashboardData)
        vpDashboardData = view.findViewById(R.id.vpDashboardData)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
