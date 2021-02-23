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
import com.arvi.Adapter.SetWfHDataAdapter
import com.arvi.R

class WfHRequestListActivity : AppCompatActivity(), View.OnClickListener {
    var imgVwBackWfhRA: ImageView?=null
    var rVwRequestWfhRA: RecyclerView?=null
    var tvNoVisitorWfhRA: TextView?=null
    var imgVwAddRequestWfhRA: ImageView?=null

    var context: Context?=null
    var snackbarView: View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wf_h_request_list)
        setIds()
        setListeners()
        setData()
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            var setDataAdapter = SetWfHDataAdapter(context!!)
            rVwRequestWfhRA!!.layoutManager =
                LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rVwRequestWfhRA!!.setAdapter(setDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        imgVwBackWfhRA!!.setOnClickListener(this)
        imgVwAddRequestWfhRA!!.setOnClickListener(this)
    }

    private fun setIds() {
        context = WfHRequestListActivity@this
        snackbarView = findViewById(android.R.id.content)
        imgVwBackWfhRA = findViewById(R.id.imgVwBackWfhRA)
        rVwRequestWfhRA = findViewById(R.id.rVwRequestWfhRA)
        tvNoVisitorWfhRA = findViewById(R.id.tvNoVisitorWfhRA)
        imgVwAddRequestWfhRA = findViewById(R.id.imgVwAddRequestWfhRA)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        when(view!!.id){
            R.id.imgVwBackWfhRA->{
                finish()
            }
            R.id.imgVwAddRequestWfhRA->{
                var intent = Intent(context!!,AddWfHRequestActivity::class.java)
                startActivity(intent)
            }
        }
    }
}