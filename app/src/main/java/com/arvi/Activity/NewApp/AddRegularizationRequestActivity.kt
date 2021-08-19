package com.arvi.Activity.NewApp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arvi.Model.GetRegularisationRequestResponseItem
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
import com.crashlytics.android.Crashlytics
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddRegularizationRequestActivity : AppCompatActivity(), View.OnClickListener {

    var imgVwBackARRA: ImageView? = null
    var spEmpNameARRA: Spinner? = null
    var etEmpIdARRA: EditText? = null
    var tvAttenDateARRA: TextView? = null
    var tvCheckInTimeARRA: TextView? = null
    var tvCheckOutTimeARRA: TextView? = null
    var tvAddRequestARRA: TextView? = null
    var tvInDateARRA: TextView? = null
    var tvOutDateARRA: TextView? = null
    var tvEmpNameARRA: TextView? = null

    var context: Context? = null
    var snackBarView: View? = null
    var requestData: GetRegularisationRequestResponseItem? = null
    var strInDate: String? = null
    var strInTime: String? = null
    var strOutDate: String? = null
    var strOutTime: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_regularization_request)
        try {
            setIds()
            setListeners()
            if (intent.extras != null) {
                requestData = intent.getParcelableExtra("requestData")
                if (requestData != null) {
                    tvEmpNameARRA!!.setText(requestData!!.user!!.name)
                    etEmpIdARRA!!.setText(requestData!!.user!!.employeeId)

                    val input = SimpleDateFormat("yyyy-MM-dd")
                    val output = SimpleDateFormat("dd/MM/yyyy")
                    var formateDateOn = input.parse(requestData!!.dateOn)
                    var showDateOn = output.format(formateDateOn)
                    tvAttenDateARRA!!.setText(showDateOn)
                    val timeZone: TimeZone = TimeZone.getTimeZone("IST")
                    if (requestData!!.inAt != null) {
                        var inDateTime = requestData!!.inAt
                        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
                        input.timeZone  = timeZone
                        val output = SimpleDateFormat("hh:mm a")
                        var formateStartdate = input.parse(inDateTime)
                        var showInTime = output.format(formateStartdate)
                        tvCheckInTimeARRA!!.setText(showInTime)

                        val outputDate = SimpleDateFormat("dd/MM/yyyy")
                        var showInDate = outputDate.format(formateStartdate)
                        tvInDateARRA!!.setText(showInDate)
                    }

                    if (requestData!!.outAt != null) {
                        var outDateTime = requestData!!.outAt
                        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
                        input.timeZone  = timeZone
                        val output = SimpleDateFormat("hh:mm a")
                        var formateEnddate = input.parse(outDateTime)
                        var showOutTime = output.format(formateEnddate)
                        tvCheckOutTimeARRA!!.setText(showOutTime)

                        val outputDate = SimpleDateFormat("dd/MM/yyyy")
                        var showOutDate = outputDate.format(formateEnddate)
                        tvOutDateARRA!!.setText(showOutDate)
                    }
                }
            }

            val empList = resources.getStringArray(R.array.default_emp)
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, empList
            )
            spEmpNameARRA!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwBackARRA!!.setOnClickListener(this)
//            tvAttenDateARRA!!.setOnClickListener(this)
            tvCheckInTimeARRA!!.setOnClickListener(this)
            tvCheckOutTimeARRA!!.setOnClickListener(this)
            tvAddRequestARRA!!.setOnClickListener(this)
            tvInDateARRA!!.setOnClickListener(this)
            tvOutDateARRA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AddRegularizationRequestActivity@ this
            snackBarView = findViewById(android.R.id.content)
            imgVwBackARRA = findViewById(R.id.imgVwBackARRA)
            spEmpNameARRA = findViewById(R.id.spEmpNameARRA)
            etEmpIdARRA = findViewById(R.id.etEmpIdARRA)
            tvAttenDateARRA = findViewById(R.id.tvAttenDateARRA)
            tvCheckInTimeARRA = findViewById(R.id.tvCheckInTimeARRA)
            tvCheckOutTimeARRA = findViewById(R.id.tvCheckOutTimeARRA)
            tvAddRequestARRA = findViewById(R.id.tvAddRequestARRA)
            tvEmpNameARRA = findViewById(R.id.tvEmpNameARRA)
            tvInDateARRA = findViewById(R.id.tvInDateARRA)
            tvOutDateARRA = findViewById(R.id.tvOutDateARRA)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        try {
            when (view!!.id) {
                R.id.imgVwBackARRA -> {
                    finish()
                }
                R.id.tvInDateARRA -> {
                    var from = "in"
                    openDatePickerDialog(from)
                }
                R.id.tvOutDateARRA -> {
                    var from = "out"
                    openDatePickerDialog(from)
                }
                /*  R.id.tvAttenDateARRA->{
                      openDatePickerDialog()
                  }*/
                R.id.tvCheckInTimeARRA -> {
                    var from = "checkIn"
                    openTimePickerDialog(from)
                }
                R.id.tvCheckOutTimeARRA -> {
                    var from = "checkOut"
                    openTimePickerDialog(from)
                }
                R.id.tvAddRequestARRA -> {
                    KeyboardUtility.hideKeyboard(context!!, tvAddRequestARRA!!)
                    if (isValidInput())
                        try {
                            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a")
                            val strIn = strInDate + " " + strInTime
                            val date1 = formatter.parse(strIn)
                            val strOut = strOutDate + " " + strOutTime
                            val date2 = formatter.parse(strOut)
                            if (date1.compareTo(date2) < 0) {
                                //println("date2 is Greater than my date1")

                                val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
                                val timeZone: TimeZone = TimeZone.getTimeZone("IST")
                                output.timeZone  = timeZone
                                val input = SimpleDateFormat("dd/MM/yyyy hh:mm a")
                                var formateInDate = input.parse(strIn)
                                var sendInAt = output.format(formateInDate)
                                Log.e("sendInAt:-",sendInAt)

                                var formateOutDate = input.parse(strOut)
                                var sendOutAt = output.format(formateOutDate)
                                Log.e("sendOutAt:-",sendOutAt)


                                if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                    callAddRegularisationApi(sendInAt,sendOutAt)
                                } else {
                                    SnackBar.showInternetError(context!!, snackBarView!!)
                                }

                            }else{
                                SnackBar.showValidationError(context!!,snackBarView!!,"Check-Out should not be before Check-In")
                            }
                        } catch (e1: ParseException) {
                            e1.printStackTrace()
                        }
                  //  SnackBar.showInProgressError(context!!, tvAddRequestARRA!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddRegularisationApi(sendInAt: String, sendOutAt: String) {
        try {
            var jsonObject = JsonObject()
            jsonObject.addProperty("dateOn", requestData!!.dateOn)
            jsonObject.addProperty("userId", requestData!!.user!!.id)
            jsonObject.addProperty("inAt", sendInAt)
            jsonObject.addProperty("outAt", sendOutAt)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.updateRegularisationRequest(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                "application/json",
                requestData!!.id!!,
                jsonObject
            )
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                var dialog = AlertDialog.Builder(context!!)
                                dialog.setTitle("Regularisation request updated successfully")
                                dialog.setCancelable(false)
                                dialog.setPositiveButton("Ok",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog!!.dismiss()
                                            finish()
                                        }
                                    })
                                dialog.show()
                            } else if(response.code() == 501){
                                Toast.makeText(context,"Report creation failed. Please try again." + response.errorBody().toString(),Toast.LENGTH_LONG).show()
                            }
                            else {
                                Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseBody>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show()
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }

    }

    private fun isValidInput(): Boolean {
        strInDate = tvInDateARRA!!.text.toString()
        strInTime = tvCheckInTimeARRA!!.text.toString()
        strOutDate = tvOutDateARRA!!.text.toString()
        strOutTime = tvCheckOutTimeARRA!!.text.toString()

        if (strInDate.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackBarView!!, "Please select check-in date")
            tvInDateARRA!!.requestFocus()
            return false
        } else if (strInTime.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackBarView!!, "Please select check-in time")
            tvCheckInTimeARRA!!.requestFocus()
            return false
        } else if (strOutDate.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackBarView!!, "Please select check-out date")
            tvOutDateARRA!!.requestFocus()
            return false
        } else if (strOutTime.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackBarView!!, "Please select check-out time")
            tvCheckOutTimeARRA!!.requestFocus()
            return false
        }

        return true
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

                    val fmtOut = SimpleDateFormat("hh:mm a")

                    val formattedTime = fmtOut.format(date)
                    if (from.equals("checkIn")) {
                        tvCheckInTimeARRA!!.setText(formattedTime)
                    } else {
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
                        showMonth = "0" + (monthOfYear+1)
                    } else {
                        showMonth = (monthOfYear+1).toString()
                    }
                    if (from.equals("out")) {
                        tvOutDateARRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    } else {
                        tvInDateARRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                    }
                },
                year,
                month,
                day
            )

            dpd.show()

            val string_date = requestData!!.dateOn
            val f = SimpleDateFormat("yyyy-MM-dd")
            try {
                val d = f.parse(string_date)
                val milliseconds = d.time
                dpd.datePicker.minDate = milliseconds

                if (from.equals("out")) {
                    var dateSelectedFrom = f.parse(string_date)
                    val MILLIS_IN_DAY = 1000 * 60 * 60 * 24
                    val nextDate: String =
                        f.format(dateSelectedFrom.time + MILLIS_IN_DAY)
                    val d1 = f.parse(nextDate)
                    val milliseconds1 = d1.time
                    dpd.datePicker.maxDate = milliseconds1
                } else {
                    dpd.datePicker.maxDate = milliseconds
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}