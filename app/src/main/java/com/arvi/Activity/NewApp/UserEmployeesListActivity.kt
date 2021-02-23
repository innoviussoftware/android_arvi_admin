package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetAllDataAdapter
import com.arvi.Adapter.UserEmployeesListAdapter
import com.arvi.R
import kotlinx.android.synthetic.main.activity_user_employees_list.*

class UserEmployeesListActivity : AppCompatActivity() {

    lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_employees_list)
        mContext = this@UserEmployeesListActivity
        setData()
    }

    private fun setData() {
        try {
            var setVisitorDataAdapter = UserEmployeesListAdapter(mContext)
            rcVwEmpliyeeListAUEL!!.layoutManager =
                LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            rcVwEmpliyeeListAUEL!!.adapter = setVisitorDataAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //OnClicked Listners

    fun backEmpolyeeList(view: View) {
        finish()
    }

    fun addEmployeeDetails(view: View) {
        var intent = Intent(mContext, EnterEmployeeDetailsActivity::class.java)
        startActivity(intent)
    }
}