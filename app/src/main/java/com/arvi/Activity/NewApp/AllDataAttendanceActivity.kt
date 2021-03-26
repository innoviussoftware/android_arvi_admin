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

class AllDataAttendanceActivity : AppCompatActivity() {
    var imgVwBackADR: ImageView? = null
    var tvDetailADAA: TextView? = null
    var rVwDataADAA: RecyclerView? = null
    var etEmpNameADAA: EditText? = null
    var context: Context? = null
    var snackbarView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_data_attendance)
        setIds()
        setData()
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        var setVisitorDataAdapter = SetAllDataAttendanceAdapter(context!!)
        rVwDataADAA!!.layoutManager =
            LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        rVwDataADAA!!.setAdapter(setVisitorDataAdapter)
    }

    private fun setIds() {
        context = AllDataAttendanceActivity@ this
        snackbarView = findViewById(android.R.id.content)
        rVwDataADAA = findViewById(R.id.rVwDataADAA)

        imgVwBackADR = findViewById(R.id.imgVwBackADR)
        tvDetailADAA = findViewById(R.id.tvDetailADAA)
        etEmpNameADAA = findViewById(R.id.etEmpNameADAA)
    }

    override fun onBackPressed() {
        finish()
    }
}