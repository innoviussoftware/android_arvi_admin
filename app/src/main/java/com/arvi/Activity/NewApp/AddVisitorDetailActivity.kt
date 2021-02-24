package com.arvi.Activity.NewApp

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.arvi.Model.Result
import com.arvi.Model.VisitorsListModel
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
import com.crashlytics.android.Crashlytics
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_add_visitor_detail.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddVisitorDetailActivity : AppCompatActivity(), View.OnClickListener {


    var imgVwBackAVDA: ImageView? = null
    var etNameAVDA: TextInputEditText? = null
    var aTvToMeetAVDA: AutoCompleteTextView? = null
    var tILVisitDateAVDA: TextInputLayout? = null
    var etVisitDateAVDA: TextInputEditText? = null
    var tILVisitTimeAVDA: TextInputLayout? = null
    var etVisitTimeAVDA: TextInputEditText? = null
    var etComingFromAVDA: TextInputEditText? = null
    var etMobileAVDA: TextInputEditText? = null
    var etPurposeAVDA: TextInputEditText? = null
    var tvSaveAVDA: TextView? = null
    var llRegisterBtnAVDA: LinearLayout? = null
    var tvRegisterAVDA: TextView? = null
    var tvDeleteAVDA: TextView? = null


    var context: Context? = null
    var snackbarView: View? = null
    var from: String = "add"
    lateinit var visitorDetails: Result
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visitor_detail)
        try {
            setIds()
            setListeners()
            if (intent.extras != null) {
                from = intent.getStringExtra("from")
                if (from == "list") {
                    tvSaveAVDA!!.visibility = View.GONE
                    llRegisterBtnAVDA!!.visibility = View.VISIBLE

                    val gson = Gson()
                    visitorDetails = gson.fromJson(intent.getStringExtra("visitorData"), Result::class.java)

                    Log.e("visitorDetails","-----===---> "+visitorDetails)
                    setVisitorData(visitorDetails)
                } else {
                    tvSaveAVDA!!.visibility = View.VISIBLE
                    llRegisterBtnAVDA!!.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVisitorData(visitorDetails: Result) {
        etNameAVDA!!.setText(visitorDetails.name)
        aTvToMeetAVDA!!.setText(visitorDetails.data!!.visitingTo!!.name)
        etVisitDateAVDA!!.setText(visitorDetails.data!!.actualEntry!!.dateOn!!)
        etVisitTimeAVDA!!.setText(visitorDetails.data!!.actualEntry!!.timeOn!!)
        etComingFromAVDA!!.setText(visitorDetails.visitor!!.data!!.company)
        etMobileAVDA!!.setText(visitorDetails.visitor!!.mobile)
        etPurposeAVDA!!.setText(visitorDetails.visitor!!.data!!.purpose)
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
                    etVisitDateAVDA!!.setText(year.toString()+"-"+showMonth+"-"+showDay)
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

    public fun openTimePickerDialog() {
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

                    val fmtOut = SimpleDateFormat("hh:mm")

                    val formattedTime = fmtOut.format(date)
                    etVisitTimeAVDA!!.setText(formattedTime)
                }, hour, minute, false
            )//Yes 24 hour time
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.log(e.toString())
        }

    }

    private fun setListeners() {
        try {
            imgVwBackAVDA!!.setOnClickListener(this)
            tvSaveAVDA!!.setOnClickListener(this)
            tILVisitDateAVDA!!.setOnClickListener(this)
            etVisitDateAVDA!!.setOnClickListener(this)
            tILVisitTimeAVDA!!.setOnClickListener(this)
            etVisitTimeAVDA!!.setOnClickListener(this)
            tvRegisterAVDA!!.setOnClickListener(this)
            tvDeleteAVDA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(view: View) {
        try {
            when (view!!.id) {
                R.id.imgVwBackAVDA -> {
                    KeyboardUtility.hideKeyboard(context!!, imgVwBackAVDA!!)
                    finish()
                }
                R.id.tvSaveAVDA -> {
                    if (isValidation()) {

                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                            callAddVisitorDetailsApi()
                        } else {
                            SnackBar.showInternetError(context!!, snackbarView!!)
                        }
                    }
                }
                R.id.tILVisitDateAVDA -> {
                    openDatePickerDialog()
                }
                R.id.etVisitDateAVDA -> {
                    openDatePickerDialog()
                }
                R.id.tILVisitTimeAVDA -> {
                    openTimePickerDialog()
                }
                R.id.etVisitTimeAVDA -> {
                    openTimePickerDialog()
                }
                R.id.tvRegisterAVDA -> {
                    openPersionVerifyDialog()

                }
                R.id.tvDeleteAVDA -> {
                    SnackBar.showInProgressError(context!!, snackbarView!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddVisitorDetailsApi() {
        try{
            var jsonObjectMain = JsonObject()


            var jsonObjectData = JsonObject()
           // var jsonObjectDataMain = JsonObject()
            jsonObjectData.addProperty("purpose", purpose)
            jsonObjectData.addProperty("company", comingFrom)


         //   var jsonObjectentryMain = JsonObject()
            var jsonObjectentry = JsonObject()
            jsonObjectentry.addProperty("actualEntryTime", visitDate+" "+visitTime)

            var jsonObjectEmployee = JsonObject()
            jsonObjectEmployee.addProperty("name", tomeet)
            jsonObjectentry.add("employee",jsonObjectEmployee)

            //jsonObjectentry.addProperty("time_of_visit", visitTime)


            jsonObjectMain.addProperty("name", name)
            jsonObjectMain.addProperty("mobile", mobile)
            jsonObjectMain.add("data",jsonObjectData)
            jsonObjectMain.add("entry",jsonObjectentry)


            Log.e("jsonObjectentry","-----==---->"+jsonObjectentry)
            Log.e("jsonObjectData","-----==---->"+jsonObjectData)
            Log.e("jsonObjectMain","-----==---->"+jsonObjectMain)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.addVisitorsEntry(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                "application/json",
                jsonObjectMain
            )
                .enqueue(object : Callback<VisitorsListModel> {

                    override fun onResponse(
                        call: Call<VisitorsListModel>,
                        response: Response<VisitorsListModel>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                val returnIntent = Intent()
                                //returnIntent.putExtra("result", result)
                                setResult(Activity.RESULT_OK, returnIntent)
                                finish()
                            } else {
                                SnackBar.showError(
                                    context!!,
                                    snackbarView!!,
                                    "Something went wrong"
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<VisitorsListModel>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }
    }

    lateinit var name: String
    lateinit var tomeet: String
    lateinit var visitDate: String
    lateinit var visitTime: String
    lateinit var comingFrom: String
    lateinit var mobile: String
    lateinit var purpose: String

    private fun isValidation(): Boolean {
        name = etNameAVDA!!.text.toString()
        tomeet = aTvToMeetAVDA!!.text.toString()
        visitDate = etVisitDateAVDA!!.text.toString()
        visitTime = etVisitTimeAVDA!!.text.toString()
        comingFrom = etComingFromAVDA!!.text.toString()
        mobile = etMobileAVDA!!.text.toString()
        purpose = etPurposeAVDA!!.text.toString()

        if (name.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter name.")
            etNameAVDA!!.requestFocus()
            return false
        } else if (tomeet.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter meet person name.")
            aTvToMeetAVDA!!.requestFocus()
            return false
        } else if (visitDate.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please select date of visit.")
            etVisitDateAVDA!!.requestFocus()
            return false
        } else if (comingFrom.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter coming from.")
            etComingFromAVDA!!.requestFocus()
            return false
        } else if (mobile.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter mobile number.")
            etMobileAVDA!!.requestFocus()
            return false
        } else if (mobile.length < 10) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter valid mobile number.")
            etMobileAVDA!!.requestFocus()
            return false
        } else if (purpose.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter purpose.")
            etPurposeAVDA!!.requestFocus()
            return false
        }

        return true
    }

    private fun openPersionVerifyDialog() {
        try {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_verify_visitor)
            var imgVwPhotoDVV = dialog.findViewById(R.id.imgVwPhotoDVV) as ImageView
            var tvNameDVV = dialog.findViewById(R.id.tvNameDVV) as TextView
            var tvCompanyDVV = dialog.findViewById(R.id.tvCompanyDVV) as TextView
            var tvLastVisitedDVV = dialog.findViewById(R.id.tvLastVisitedDVV) as TextView
            var tvSamePersonDVV = dialog.findViewById(R.id.tvSamePersonDVV) as TextView
            var tvChangeDVV = dialog.findViewById(R.id.tvChangeDVV) as TextView
            tvSamePersonDVV.setOnClickListener {
                dialog.dismiss()
                openVisitorRegisterSuccessDialog()
            }
            tvChangeDVV.setOnClickListener {
                var intent = Intent(context!!, AddVisitorPhotoActivity::class.java)
                startActivity(intent)
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openVisitorRegisterSuccessDialog() {
        try {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_visitor_register_success)
            dialog.setCancelable(false)
            var imgVwPhotoDVRS = dialog.findViewById(R.id.imgVwPhotoDVRS) as ImageView
            var tvNameDVRS = dialog.findViewById(R.id.tvNameDVRS) as TextView
            var tvOkDVRS = dialog.findViewById(R.id.tvOkDVRS) as TextView
            tvOkDVRS.setOnClickListener {
                dialog.dismiss()
                finish()
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AddVisitorDetailActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackAVDA = findViewById(R.id.imgVwBackAVDA)
            etNameAVDA = findViewById(R.id.etNameAVDA)
            aTvToMeetAVDA = findViewById(R.id.aTvToMeetAVDA)
            tILVisitDateAVDA = findViewById(R.id.tILVisitDateAVDA)
            etVisitDateAVDA = findViewById(R.id.etVisitDateAVDA)
            tILVisitTimeAVDA = findViewById(R.id.tILVisitTimeAVDA)
            etVisitTimeAVDA = findViewById(R.id.etVisitTimeAVDA)
            etComingFromAVDA = findViewById(R.id.etComingFromAVDA)
            etMobileAVDA = findViewById(R.id.etMobileAVDA)
            etPurposeAVDA = findViewById(R.id.etPurposeAVDA)
            tvSaveAVDA = findViewById(R.id.tvSaveAVDA)
            llRegisterBtnAVDA = findViewById(R.id.llRegisterBtnAVDA)
            tvRegisterAVDA = findViewById(R.id.tvRegisterAVDA)
            tvDeleteAVDA = findViewById(R.id.tvDeleteAVDA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        KeyboardUtility.hideKeyboard(context!!, imgVwBackAVDA!!)
        finish()
    }
}
