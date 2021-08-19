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

class AddWfHRequestActivity : AppCompatActivity(), View.OnClickListener {

    var imgVwBackAWRA: ImageView? = null
    var spEmpNameAWRA: Spinner? = null
    var etEmpIdAWRA: EditText? = null
    var tvStartDateAWRA: TextView? = null
    var tvEndDateAWRA: TextView? = null
    var tvAddRequestAWRA: TextView? = null

    var context: Context? = null
    var snackBarView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wf_h_request)
        try {
            setIds()
            setListeners()


            val empList = resources.getStringArray(R.array.default_emp)
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,empList)
            spEmpNameAWRA!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwBackAWRA!!.setOnClickListener(this)
            tvStartDateAWRA!!.setOnClickListener(this)
            tvEndDateAWRA!!.setOnClickListener(this)
            tvAddRequestAWRA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AddWfHRequestActivity@this
            snackBarView = findViewById(android.R.id.content)
            imgVwBackAWRA = findViewById(R.id.imgVwBackAWRA)
            spEmpNameAWRA = findViewById(R.id.spEmpNameAWRA)
            etEmpIdAWRA = findViewById(R.id.etEmpIdAWRA)
            tvStartDateAWRA = findViewById(R.id.tvStartDateAWRA)
            tvEndDateAWRA = findViewById(R.id.tvEndDateAWRA)
            tvAddRequestAWRA = findViewById(R.id.tvAddRequestAWRA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        try {
            when(view!!.id){
                R.id.imgVwBackAWRA->{
                    finish()
                }
                R.id.tvStartDateAWRA->{
                    var from  = "startDate"
                    openDatePickerDialog(from)
                }
                R.id.tvEndDateAWRA->{
                    var from  = "endDate"
                    openDatePickerDialog(from)
                }
                R.id.tvAddRequestAWRA->{
                    SnackBar.showInProgressError(context!!,snackBarView!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                        tvStartDateAWRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    }else{
                        tvEndDateAWRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
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