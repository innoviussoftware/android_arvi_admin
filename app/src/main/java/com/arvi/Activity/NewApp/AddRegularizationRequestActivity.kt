package com.arvi.Activity.NewApp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.arvi.R
import com.arvi.Utils.SnackBar
import com.crashlytics.android.Crashlytics
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddRegularizationRequestActivity : AppCompatActivity(), View.OnClickListener {

    var imgVwBackARRA: ImageView? = null
    var spEmpNameARRA: Spinner? = null
    var etEmpIdARRA: EditText? = null
    var tvDateARRA: TextView? = null
    var tvCheckInTimeARRA: TextView? = null
    var tvCheckOutTimeARRA: TextView? = null
    var tvAddRequestARRA:TextView?=null

    var context:Context?=null
    var snackBarView:View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_regularization_request)
        try {
            setIds()
            setListeners()

            val empList = resources.getStringArray(R.array.default_emp)
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,empList)
            spEmpNameARRA!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwBackARRA!!.setOnClickListener(this)
            tvDateARRA!!.setOnClickListener(this)
            tvCheckInTimeARRA!!.setOnClickListener(this)
            tvCheckOutTimeARRA!!.setOnClickListener(this)
            tvAddRequestARRA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AddRegularizationRequestActivity@this
            imgVwBackARRA = findViewById(R.id.imgVwBackARRA)
            spEmpNameARRA = findViewById(R.id.spEmpNameARRA)
            etEmpIdARRA = findViewById(R.id.etEmpIdARRA)
            tvDateARRA = findViewById(R.id.tvDateARRA)
            tvCheckInTimeARRA = findViewById(R.id.tvCheckInTimeARRA)
            tvCheckOutTimeARRA = findViewById(R.id.tvCheckOutTimeARRA)
            tvAddRequestARRA = findViewById(R.id.tvAddRequestARRA)
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
                R.id.imgVwBackARRA->{
                    finish()
                }
                R.id.tvDateARRA->{
                    openDatePickerDialog()
                }
                R.id.tvCheckInTimeARRA->{
                    var from = "checkIn"
                    openTimePickerDialog(from)
                }
                R.id.tvCheckOutTimeARRA->{
                    var from = "checkOut"
                    openTimePickerDialog(from)
                }
                R.id.tvAddRequestARRA->{
                    SnackBar.showInProgressError(context!!,tvAddRequestARRA!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openTimePickerDialog(from: String) {
        try {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mcurrentTime.get(Calendar.MINUTE)
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                context,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    val time = "$selectedHour:$selectedMinute"

                    val fmt = SimpleDateFormat("HH:mm")
                    var date: Date? = null
                    try {
                        date = fmt.parse(time)
                    } catch (e: ParseException) {

                        e.printStackTrace()
                        Crashlytics.log(e.toString())
                    }

                    val fmtOut = SimpleDateFormat("hh:mm aa")

                    val formattedTime = fmtOut.format(date)
                    if (from.equals("checkIn")) {
                        tvCheckInTimeARRA!!.setText(formattedTime)
                    }else{
                        tvCheckOutTimeARRA!!.setText(formattedTime)
                    }
                }, hour, minute, false
            )//Yes 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.toString())
        }

    }

    private fun openDatePickerDialog() {
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
                    tvDateARRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
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