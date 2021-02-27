package com.arvi.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.arvi.Adapter.SetVisitorDataAdapter
import com.arvi.Model.VisitorsListModel
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.arvi.Activity.NewApp.AddVisitorDetailActivity
import com.arvi.Model.Result
import com.google.gson.Gson
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class VisitorListFragment : Fragment(){


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
        var view = inflater.inflate(R.layout.fragment_visitor_list, container, false)
        try {
            setIds(view)
            configrationTabBar()
/*

            if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                callGetVisitorListAPI()
                //callLoginAPI(strKioskId)
            } else {
                SnackBar.showInternetError(appContext!!, snackbarView!!)
            }
*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }



    private fun configrationTabBar(){
        setupViewPager(vwPgrVisitorFVL!!)
        tbLytVisitorTabFVL!!.setupWithViewPager(vwPgrVisitorFVL)

        try {
            //Todo: Custome Tab view : means Text and Horizontal View
            for (i in 0 until tbLytVisitorTabFVL.tabCount) {
                val tab: TabLayout.Tab = tbLytVisitorTabFVL.getTabAt(i)!!
                val relativeLayout: LinearLayout = LayoutInflater.from(appContext).inflate(R.layout.my_visitor_tab_layout, tbLytVisitorTabFVL, false) as LinearLayout

                val tabTextView = relativeLayout.findViewById(R.id.tvAdsTitle) as TextView
                //  val tab_view=relativeLayout.findViewById(R.id.ivDownFilter) as ImageView

                tabTextView.text = tab.text
                tab.customView=relativeLayout
                //tbLytVisitorTabFVL.selectTab(tbLytVisitorTabFVL.getTabAt(2))

                if(i==0){
                    tabTextView.setTextColor(resources.getColor(R.color.app_bg))
                }
            }

            vwPgrVisitorFVL.currentItem=tbLytVisitorTabFVL.selectedTabPosition

            tbLytVisitorTabFVL.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
    private fun setupViewPager(vwPgrVisitorFVL: ViewPager) {
        try {
            val adapter = ViewPagerAdapter(childFragmentManager)
            adapter.addFragment(ExpectedVisitorFragment(), appContext!!.resources.getString(R.string.expected_ttl))
            adapter.addFragment(ScreenedVisitorFragment(), appContext!!.resources.getString(R.string.screened_ttl))
            //  adapter.addFragment(AdsTabFragment(), appContext.resources.getString(R.string.view_ads_ttl))
            vwPgrVisitorFVL.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

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





    lateinit var tbLytVisitorTabFVL: TabLayout
    lateinit var vwPgrVisitorFVL:ViewPager

    private fun setIds(view: View) {
        try {
            appContext = activity
            snackbarView = activity?.findViewById(android.R.id.content)
/*            rVwVisitorVLF = view.findViewById(R.id.rVwVisitorVLF)
            tvNoVisitorVLF = view.findViewById(R.id.tvNoVisitorVLF)
            imgVwAddVisitorVLF = view.findViewById(R.id.imgVwAddVisitorVLF)*/

            tbLytVisitorTabFVL=view.findViewById(R.id.tbLytVisitorTabFVL)
            vwPgrVisitorFVL=view.findViewById(R.id.vwPgrVisitorFVL)
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

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VisitorListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
