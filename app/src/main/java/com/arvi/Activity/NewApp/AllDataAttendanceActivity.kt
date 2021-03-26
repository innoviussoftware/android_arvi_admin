package com.arvi.Activity.NewApp

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetAllDataAdapter
import com.arvi.Adapter.SetAllDataAttendanceAdapter
import com.arvi.R

class AllDataAttendanceActivity : AppCompatActivity(), View.OnClickListener {
    var imgVwBackADAA: ImageView? = null
    var tvDetailADAA: TextView? = null
    var rVwDataADAA: RecyclerView? = null
    var etEmpNameADAA: EditText? = null
    var context: Context? = null
    var snackbarView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_data_attendance)
        try {
            setIds()
            setListeners()
            setData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwBackADAA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            var setVisitorDataAdapter = SetAllDataAttendanceAdapter(context!!)
            rVwDataADAA!!.layoutManager =
                LinearLayoutManager(context, LinearLayout.VERTICAL, false)
            rVwDataADAA!!.setAdapter(setVisitorDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AllDataAttendanceActivity@ this
            snackbarView = findViewById(android.R.id.content)
            rVwDataADAA = findViewById(R.id.rVwDataADAA)

            imgVwBackADAA = findViewById(R.id.imgVwBackADAA)
            tvDetailADAA = findViewById(R.id.tvDetailADAA)
            etEmpNameADAA = findViewById(R.id.etEmpNameADAA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.imgVwBackADAA -> {
                    finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}