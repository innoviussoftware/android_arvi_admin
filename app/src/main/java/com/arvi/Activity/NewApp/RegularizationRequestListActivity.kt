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
import com.arvi.Adapter.SetRegularizationDataAdapter
import com.arvi.Adapter.SetVisitorDataAdapter
import com.arvi.R

class RegularizationRequestListActivity : AppCompatActivity(), View.OnClickListener {

    var imgVwBackRRLA:ImageView?=null
    var rVwRequestRRLA:RecyclerView?=null
    var tvNoVisitorRRLA:TextView?=null
    var imgVwAddRequestRRLA:ImageView?=null

    var context:Context?=null
    var snackbarView: View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regularization_request_list)
        setIds()
        setListeners()
        setData()
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            var setVisitorDataAdapter = SetRegularizationDataAdapter(context!!)
            rVwRequestRRLA!!.layoutManager =
                LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rVwRequestRRLA!!.setAdapter(setVisitorDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        imgVwBackRRLA!!.setOnClickListener(this)
        imgVwAddRequestRRLA!!.setOnClickListener(this)
    }

    private fun setIds() {
        context = RegularizationRequestListActivity@this
        snackbarView = findViewById(android.R.id.content)
        imgVwBackRRLA = findViewById(R.id.imgVwBackRRLA)
        rVwRequestRRLA = findViewById(R.id.rVwRequestRRLA)
        tvNoVisitorRRLA = findViewById(R.id.tvNoVisitorRRLA)
        imgVwAddRequestRRLA = findViewById(R.id.imgVwAddRequestRRLA)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.imgVwBackRRLA->{
                finish()
            }
            R.id.imgVwAddRequestRRLA->{
                var intent = Intent(context,AddRegularizationRequestActivity::class.java)
                startActivity(intent)
            }
        }
    }
}