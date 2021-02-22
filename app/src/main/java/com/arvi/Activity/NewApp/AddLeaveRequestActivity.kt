package com.arvi.Activity.NewApp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.arvi.R

class AddLeaveRequestActivity : AppCompatActivity() {
    var imgVwBackALRA:ImageView?=null
    var spEmpNameARRA:Spinner?=null
    var etEmpIdALRA:EditText?=null
    var tvStartDateALRA:TextView?=null
    var tvEndDateALRA:TextView?=null
    var spLeaveTypeALRA:Spinner?=null
    var etLeaveReasonALRA:EditText?=null
    var tvAddRequestALRA:TextView?=null

    var context:Context?=null
    var snackbarView: View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leave_request)
        setIds()
    }

    private fun setIds() {
         imgVwBackALRA = findViewById(R.id.imgVwBackALRA)
        imgVwBackALRA = findViewById(R.id.imgVwBackALRA)
         etEmpIdALRA = findViewById(R.id.etEmpIdALRA)
         tvStartDateALRA = findViewById(R.id.tvStartDateALRA)
         tvEndDateALRA= findViewById(R.id.tvEndDateALRA)
         spLeaveTypeALRA = findViewById(R.id.spLeaveTypeALRA)
         etLeaveReasonALRA = findViewById(R.id.etLeaveReasonALRA)
         tvAddRequestALRA = findViewById(R.id.tvAddRequestALRA)
         context = AddLeaveRequestActivity@this
         snackbarView = findViewById(android.R.id.content)
    }
}