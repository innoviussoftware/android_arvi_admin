package com.arvi.Activity.NewApp

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.arvi.R
import com.arvi.Utils.SnackBar
import java.util.*

class AddLeaveRequestActivity : AppCompatActivity(), View.OnClickListener {
    var imgVwBackALRA: ImageView? = null
    var spEmpNameALRA: Spinner? = null
    var etEmpIdALRA: EditText? = null
    var tvStartDateALRA: TextView? = null
    var tvEndDateALRA: TextView? = null
    var spLeaveTypeALRA: Spinner? = null
    var etLeaveReasonALRA: EditText? = null
    var tvAddRequestALRA: TextView? = null

    var context: Context? = null
    var snackbarView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leave_request)
        try {
            setIds()
            setListeners()

            val empList = resources.getStringArray(R.array.default_emp)
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, empList
            )
            spEmpNameALRA!!.adapter = adapter

            val leavetypeList = resources.getStringArray(R.array.default_leave_type)
            val adapterLeaveType = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,leavetypeList)
            spLeaveTypeALRA!!.adapter = adapterLeaveType
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwBackALRA!!.setOnClickListener(this)
            tvStartDateALRA!!.setOnClickListener(this)
            tvEndDateALRA!!.setOnClickListener(this)
            tvAddRequestALRA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            imgVwBackALRA = findViewById(R.id.imgVwBackALRA)
            spEmpNameALRA = findViewById(R.id.spEmpNameALRA)
            etEmpIdALRA = findViewById(R.id.etEmpIdALRA)
            tvStartDateALRA = findViewById(R.id.tvStartDateALRA)
            tvEndDateALRA = findViewById(R.id.tvEndDateALRA)
            spLeaveTypeALRA = findViewById(R.id.spLeaveTypeALRA)
            etLeaveReasonALRA = findViewById(R.id.etLeaveReasonALRA)
            tvAddRequestALRA = findViewById(R.id.tvAddRequestALRA)
            context = AddLeaveRequestActivity@ this
            snackbarView = findViewById(android.R.id.content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgVwBackALRA -> {
                finish()
            }
            R.id.tvStartDateALRA -> {
                var from  = "startDate"
                openDatePickerDialog(from)
            }
            R.id.tvEndDateALRA -> {
                var from  = "endDate"
                openDatePickerDialog(from)
            }
            R.id.tvAddRequestALRA -> {
                SnackBar.showInProgressError(context!!,tvAddRequestALRA!!)
            }
        }
    }

    private fun openDatePickerDialog(from: String) {
        try {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)


            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var showDay = "01"
                    var showMonth = "01"
                    if (dayOfMonth < 10) {
                        showDay = "0" + dayOfMonth
                    } else {
                        showDay = dayOfMonth.toString()
                    }

                    if (monthOfYear < 10) {
                        showMonth = "0" + monthOfYear
                    } else {
                        showMonth = monthOfYear.toString()
                    }
                    if (from.equals("startDate")) {
                        tvStartDateALRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    }else{
                        tvEndDateALRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    }
                },
                year,
                month,
                day
            )

            dpd.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}