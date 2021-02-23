package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.arvi.R

class EnterEmployeeDetailsActivity : AppCompatActivity(), View.OnClickListener {

    var imgVwBackEEDA: ImageView? = null
    var etNameEEDA: EditText? = null
    var etEmployeeIDEEDA: EditText? = null
    var etDesignationEEDA: EditText? = null
    var etMobileEEDA: EditText? = null
    var etMailEEDA: EditText? = null
    var tvTakePicEEDA: TextView? = null

    var context: Context? = null
    var snackbarView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee_details)
        setIds()
        setListener()
    }

    private fun setListener() {
        imgVwBackEEDA!!.setOnClickListener(this)
        tvTakePicEEDA!!.setOnClickListener(this)
    }

    private fun setIds() {
        context = EnterEmployeeDetailsActivity@ this
        snackbarView = findViewById(android.R.id.content)
        imgVwBackEEDA = findViewById(R.id.imgVwBackEEDA)
        etNameEEDA = findViewById(R.id.etNameEEDA)
        etEmployeeIDEEDA = findViewById(R.id.etEmployeeIDEEDA)
        etDesignationEEDA = findViewById(R.id.etDesignationEEDA)
        etMobileEEDA = findViewById(R.id.etMobileEEDA)
        etMailEEDA = findViewById(R.id.etMailEEDA)
        tvTakePicEEDA = findViewById(R.id.tvTakePicEEDA)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgVwBackEEDA -> {
                finish()
            }
            R.id.tvTakePicEEDA -> {
                var intent = Intent(context!!,TakeEmployeePicActivity::class.java)
                startActivity(intent)
            }
        }
    }

}