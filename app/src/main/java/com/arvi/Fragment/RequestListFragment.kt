package com.arvi.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.arvi.R
import com.google.android.material.tabs.TabLayout

class RequestListFragment : Fragment() {

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
        var view = inflater.inflate(R.layout.fragment_request_list, container, false)
        try {
            setIds(view)
            configrationTabBar()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    private fun configrationTabBar() {
        setupViewPager(vwPgrRequestFVL!!)
        tbLytRequestTabFVL!!.setupWithViewPager(vwPgrRequestFVL)

        try {
            //Todo: Custome Tab view : means Text and Horizontal View
            for (i in 0 until tbLytRequestTabFVL.tabCount) {
                val tab: TabLayout.Tab = tbLytRequestTabFVL.getTabAt(i)!!
                val relativeLayout: LinearLayout = LayoutInflater.from(appContext).inflate(
                    R.layout.my_visitor_tab_layout,
                    tbLytRequestTabFVL,
                    false
                ) as LinearLayout

                val tabTextView = relativeLayout.findViewById(R.id.tvAdsTitle) as TextView
                //  val tab_view=relativeLayout.findViewById(R.id.ivDownFilter) as ImageView

                tabTextView.text = tab.text
                tab.customView = relativeLayout


                if (i == 0) {
                    tabTextView.setTextColor(resources.getColor(R.color.app_bg))
                }
            }

            vwPgrRequestFVL.currentItem = tbLytRequestTabFVL.selectedTabPosition

            tbLytRequestTabFVL.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    val tabView: View = tab!!.getCustomView()!!
                    val tabTextView = tabView.findViewById(R.id.tvAdsTitle) as TextView

                    //tab_label.visibility=View.GONE
                    tabTextView.setTextColor(resources.getColor(R.color.gray_clr))

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val tabView = tab!!.customView
                    val tabTextView = tabView!!.findViewById(R.id.tvAdsTitle) as TextView

                    //tab_label.visibility=View.GONE
                    tabTextView.setTextColor(resources.getColor(R.color.app_bg))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //Todo:Start=> Tablayout set view pager tab and tab title/and values
    private fun setupViewPager(vwPgrRequestFVL: ViewPager) {
        try {
            val adapter = ViewPagerAdapter(childFragmentManager)
            adapter.addFragment(
                LeaveListFragment(),
                appContext!!.resources.getString(R.string.leave_ttl)
            )
            adapter.addFragment(
                WFHListFragment(),
                appContext!!.resources.getString(R.string.wfh_ttl)
            )
            adapter.addFragment(
                RegularizationRequestFragment(),
                appContext!!.resources.getString(R.string.regularization_ttl)
            )
            vwPgrRequestFVL.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitleList[position]
        }
    }


    lateinit var tbLytRequestTabFVL: TabLayout
    lateinit var vwPgrRequestFVL: ViewPager

    private fun setIds(view: View) {
        try {
            appContext = activity
            snackbarView = activity?.findViewById(android.R.id.content)
            tbLytRequestTabFVL = view.findViewById(R.id.tbLytRequestTabFVL)
            vwPgrRequestFVL = view.findViewById(R.id.vwPgrRequestFVL)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            RequestListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}