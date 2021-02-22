package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.arvi.Fragment.DashboardFragment
import com.arvi.Fragment.ProfileFragment
import com.arvi.Fragment.RequestTypeFragment
import com.arvi.Fragment.VisitorListFragment
import com.arvi.R
import com.arvi.Utils.SnackBar
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DashboardActivity : AppCompatActivity(), View.OnClickListener {

    var containerBody : FrameLayout?=null
    var rlDashboardDA: RelativeLayout?=null
    var imgVwDashboardDA:ImageView?=null
    var tvDashboardTitleDA:TextView?=null

    var rlSelfiCheckInDA: RelativeLayout?=null
    var imgVwSelfiCheckInDA:ImageView?=null
    var tvSelfiCheckInDA:TextView?=null

    var rlVisitorDA: RelativeLayout?=null
    var imgVwVisitorDA:ImageView?=null
    var tvVisitorTitleDA:TextView?=null

    var rlRequestDA: RelativeLayout?=null
    var imgVwRequestDA:ImageView?=null
    var tvRequestTitleDA:TextView?=null

    var rlProfileDA: RelativeLayout?=null
    var imgVwProfileDA:ImageView?=null
    var tvProfileDA:TextView?=null


    var context: Context?=null
    var snackbarView: View?=null
    private var fragmentStack: Stack<Fragment>? = null
    var fragment: Fragment? = null
    private var mContentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        try {
            setIds()
            setListeners()
            setDashboardPage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDashboardPage() {
        try {
            setDashboardIconActive()
            fragment = DashboardFragment.newInstance("", "")
            if (fragment != null) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.containerBody,
                    fragment!!,
                    DashboardFragment::class.java.getSimpleName()
                )
                fragmentStack!!.push(fragment)
                fragmentTransaction.commit()
                mContentFragment = fragment
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDashboardIconActive() {
        try {
            imgVwDashboardDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dashboard_selected))
            tvDashboardTitleDA!!.setTextColor(resources.getColor(R.color.app_bg))

            imgVwSelfiCheckInDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_selfi_unselected))
            tvSelfiCheckInDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwVisitorDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_visitor_unselected))
            tvVisitorTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwRequestDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_request_unselected))
            tvRequestTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwProfileDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_profile_unselected))
            tvProfileDA!!.setTextColor(resources.getColor(R.color.black))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setVisitorPage() {
        try {
            setVisitorIconActive()
            fragment = VisitorListFragment.newInstance("", "")
            if (fragment != null) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.containerBody,
                    fragment!!,
                    VisitorListFragment::class.java.getSimpleName()
                )
                fragmentStack!!.push(fragment)
                fragmentTransaction.commit()
                mContentFragment = fragment
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setVisitorIconActive() {
        try {
            imgVwDashboardDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dashboard_unselect))
            tvDashboardTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwSelfiCheckInDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_selfi_unselected))
            tvSelfiCheckInDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwVisitorDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_visitor_selected))
            tvVisitorTitleDA!!.setTextColor(resources.getColor(R.color.app_bg))

            imgVwRequestDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_request_unselected))
            tvRequestTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwProfileDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_profile_unselected))
            tvProfileDA!!.setTextColor(resources.getColor(R.color.black))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setRequestTypePage() {
        try {
            setRequestIconActive()
            fragment = RequestTypeFragment.newInstance("", "")
            if (fragment != null) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.containerBody,
                    fragment!!,
                    RequestTypeFragment::class.java.getSimpleName()
                )
                fragmentStack!!.push(fragment)
                fragmentTransaction.commit()
                mContentFragment = fragment
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRequestIconActive() {
        try {
            imgVwDashboardDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dashboard_unselect))
            tvDashboardTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwSelfiCheckInDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_selfi_unselected))
            tvSelfiCheckInDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwVisitorDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_visitor_unselected))
            tvVisitorTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwRequestDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_request_selected))
            tvRequestTitleDA!!.setTextColor(resources.getColor(R.color.app_bg))

            imgVwProfileDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_profile_unselected))
            tvProfileDA!!.setTextColor(resources.getColor(R.color.black))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setProfilePage() {
        try {
            setProfileIconActive()
            fragment = ProfileFragment.newInstance("", "")
            if (fragment != null) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.containerBody,
                    fragment!!,
                    ProfileFragment::class.java.getSimpleName()
                )
                fragmentStack!!.push(fragment)
                fragmentTransaction.commit()
                mContentFragment = fragment
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setProfileIconActive() {
        try {
            imgVwDashboardDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_dashboard_unselect))
            tvDashboardTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwSelfiCheckInDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_selfi_unselected))
            tvSelfiCheckInDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwVisitorDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_visitor_unselected))
            tvVisitorTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwRequestDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_request_unselected))
            tvRequestTitleDA!!.setTextColor(resources.getColor(R.color.black))

            imgVwProfileDA!!.setImageDrawable(resources.getDrawable(R.drawable.ic_profile_selected))
            tvProfileDA!!.setTextColor(resources.getColor(R.color.app_bg))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun setListeners() {
        try {
            rlDashboardDA!!.setOnClickListener(this)
            rlSelfiCheckInDA!!.setOnClickListener(this)
            rlVisitorDA!!.setOnClickListener(this)
            rlRequestDA!!.setOnClickListener(this)
            rlProfileDA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = DashboardActivity@this
            snackbarView = findViewById(android.R.id.content)

            containerBody = findViewById(R.id.containerBody)
            rlDashboardDA= findViewById(R.id.rlDashboardDA)
            imgVwDashboardDA= findViewById(R.id.imgVwDashboardDA)
            tvDashboardTitleDA= findViewById(R.id.tvDashboardTitleDA)

            rlSelfiCheckInDA= findViewById(R.id.rlSelfiCheckInDA)
            imgVwSelfiCheckInDA= findViewById(R.id.imgVwSelfiCheckInDA)
            tvSelfiCheckInDA= findViewById(R.id.tvSelfiCheckInDA)

            rlVisitorDA= findViewById(R.id.rlVisitorDA)
            imgVwVisitorDA= findViewById(R.id.imgVwVisitorDA)
            tvVisitorTitleDA= findViewById(R.id.tvVisitorTitleDA)

            rlRequestDA= findViewById(R.id.rlRequestDA)
            imgVwRequestDA= findViewById(R.id.imgVwRequestDA)
            tvRequestTitleDA= findViewById(R.id.tvRequestTitleDA)

            rlProfileDA= findViewById(R.id.rlProfileDA)
            imgVwProfileDA= findViewById(R.id.imgVwProfileDA)
            tvProfileDA= findViewById(R.id.tvProfileDA)

            fragmentStack = Stack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        try {
            when(view!!.id){
                R.id.rlDashboardDA->{
                    setDashboardPage()
                }
                R.id.rlSelfiCheckInDA->{
                    openSelfiCheckInPage()
                }
                R.id.rlVisitorDA->{
                    setVisitorPage()
                }
                R.id.rlRequestDA->{
                    setRequestTypePage()
                }
                R.id.rlProfileDA->{
                    setProfilePage()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openSelfiCheckInPage() {
        var intent = Intent(context!!,SelfiCheckInActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        finish()
    }
}
