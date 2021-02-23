package com.arvi.Activity.NewApp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetLeaveRequestDataAdapter
import com.arvi.Adapter.SetWfHDataAdapter
import com.arvi.R

class LeaveRequestListActivity : AppCompatActivity(), View.OnClickListener {
    var imgVwBackLRLA: ImageView?=null
    var rVwRequestLRLA: RecyclerView?=null
    var tvNoVisitorLRLA: TextView?=null
    var imgVwAddRequestLRLA: ImageView?=null

    var context: Context?=null
    var snackbarView: View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_request_list)
        setIds()
        setListeners()
        setData()
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            var setDataAdapter = SetLeaveRequestDataAdapter(context!!)
            rVwRequestLRLA!!.layoutManager =
                LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rVwRequestLRLA!!.setAdapter(setDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        imgVwBackLRLA!!.setOnClickListener(this)
        imgVwAddRequestLRLA!!.setOnClickListener(this)
    }

    private fun setIds() {
        context = LeaveRequestListActivity@this
        snackbarView = findViewById(android.R.id.content)
        imgVwBackLRLA = findViewById(R.id.imgVwBackLRLA)
        rVwRequestLRLA = findViewById(R.id.rVwRequestLRLA)
        tvNoVisitorLRLA = findViewById(R.id.tvNoVisitorLRLA)
        imgVwAddRequestLRLA = findViewById(R.id.imgVwAddRequestLRLA)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.imgVwBackLRLA->{
                finish()
            }
            R.id.imgVwAddRequestLRLA->{
                var intent = Intent(context!!,AddLeaveRequestActivity::class.java)
                startActivity(intent)
            }
        }
    }
}