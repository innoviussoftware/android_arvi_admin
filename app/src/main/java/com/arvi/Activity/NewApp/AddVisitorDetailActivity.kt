package com.arvi.Activity.NewApp

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.arvi.Model.CheckMobileNoResponse
import com.arvi.Model.CheckMobileNoResult
import com.arvi.Model.GetVisitorListResult
import com.arvi.Model.VisitorsListModel
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
import com.arvihealthscanner.Model.GetEmployeeListResponse
import com.arvihealthscanner.Model.GetEmployeeListResult
import com.crashlytics.android.Crashlytics
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.societyguard.Utils.FileUtil.isValidName
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class AddVisitorDetailActivity : AppCompatActivity(), View.OnClickListener {


    private var meetToId: Int? = 0
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
    var spMeetAVDA: Spinner? = null
    var imgVwSVisitorPhotoAVDA: ImageView? = null
    private var alComapniesUserList: ArrayList<GetEmployeeListResult> = ArrayList()


    var context: Context? = null
    var snackbarView: View? = null
    var from: String = "add"
    var visitorDetails: GetVisitorListResult? = null

    lateinit var className: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visitor_detail)
        try {
            setIds()
            ///test()
            setListeners()



            if (intent.extras != null) {
                from = intent.getStringExtra("from")!!
                className = intent.getStringExtra("className")!!
                if (from == "list") {
                    tvSaveAVDA!!.visibility = View.GONE
                    llRegisterBtnAVDA!!.visibility = View.VISIBLE

                    val gson = Gson()
                    visitorDetails =
                        gson.fromJson(
                            intent.getStringExtra("visitorData"),
                            GetVisitorListResult::class.java
                        )

                    Log.e("visitorDetails", "-----===---> " + visitorDetails)
                    setVisitorData(visitorDetails!!)
                } else {
                    tvSaveAVDA!!.visibility = View.VISIBLE
                    llRegisterBtnAVDA!!.visibility = View.GONE
                }
            }

            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                callGetEmployeeListAPI()
            } else {
                SnackBar.showInternetError(context!!, snackbarView!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callGetEmployeeListAPI() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.getComaniesUsersList(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    context!!
                )
            )
                .enqueue(object : Callback<GetEmployeeListResponse> {

                    override fun onResponse(
                        call: Call<GetEmployeeListResponse>,
                        response: Response<GetEmployeeListResponse>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {

                                    try {
                                        alComapniesUserList = ArrayList()
                                        alComapniesUserList.addAll(response.body().result!!)

                                        if (alComapniesUserList != null && alComapniesUserList.size > 0) {

                                            setEmpSpinnerData()

                                        } else {

                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                            } else if (response.code() == 401) {
                                var intent =
                                    Intent(context!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(context!!)
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
                        call: Call<GetEmployeeListResponse>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setEmpSpinnerData() {
        try {
            val adapter = ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_dropdown_item,
                alComapniesUserList
            )
            spMeetAVDA!!.adapter = adapter
            spMeetAVDA!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    meetToId = alComapniesUserList.get(position).id
                    tomeet = alComapniesUserList.get(position).name!!
                }
            })
            if (visitorDetails!!.data != null) {
                if (visitorDetails!!.data.visitingTo != null) {
                    var visitingTo =
                        visitorDetails!!.data.visitingTo.name + " - " + visitorDetails!!.data.visitingTo.employeeId
                    spMeetAVDA!!.setSelection(
                        getIndex(
                            spMeetAVDA!!,
                            visitingTo
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setVisitorData(visitorDetails: GetVisitorListResult) {
        try {

            var expectedEntryDateTime: String = ""
            if (className.equals("expected")) {
                expectedEntryDateTime = visitorDetails!!.data!!.expectedEntryTime
                etNameAVDA!!.setText(visitorDetails!!.data.visitor.name)
                etMobileAVDA!!.setText(visitorDetails.data.visitor.mobile)

            } else {
                llRegisterBtnAVDA!!.visibility = View.GONE
                tvSaveAVDA!!.visibility = View.GONE
                expectedEntryDateTime = visitorDetails!!.data!!.actualEntryTime
                etNameAVDA!!.setText(visitorDetails!!.visitor!!.name)
                etMobileAVDA!!.setText(visitorDetails.visitor!!.mobile)

            }


            if (expectedEntryDateTime != null) {
                if (expectedEntryDateTime.contains("T")) {
                    etVisitDateAVDA!!.setText(
                        expectedEntryDateTime.substring(
                            0,
                            expectedEntryDateTime.indexOf("T")
                        )
                    )

                    var strTime = expectedEntryDateTime.substring(
                        expectedEntryDateTime.indexOf("T") + 1,
                        expectedEntryDateTime.length - 1
                    )

                    val output = SimpleDateFormat("hh:mm a")

                    val input = SimpleDateFormat("HH:mm:ss")
                    val timeZone: TimeZone = TimeZone.getTimeZone("IST")
                    input.timeZone = timeZone

                    var formateStartdate = input.parse(strTime)
                    var showTime = output.format(formateStartdate)
                    showTime = GlobalMethods.convertUTCToISTTime(expectedEntryDateTime)
                    etVisitTimeAVDA!!.setText(showTime)
                } else {
                    etVisitDateAVDA!!.setText(
                        expectedEntryDateTime.substring(
                            0,
                            expectedEntryDateTime.indexOf(" ")
                        )
                    )
                    etVisitTimeAVDA!!.setText(
                        expectedEntryDateTime.substring(
                            expectedEntryDateTime.indexOf(
                                " "
                            ), expectedEntryDateTime.length
                        )
                    )
                }
            }
            etComingFromAVDA!!.setText(visitorDetails!!.data!!.comingFrom)

            etPurposeAVDA!!.setText(visitorDetails!!.data!!.purpose)
            if (className == "screended") {
                aTvToMeetAVDA!!.setText(visitorDetails!!.data!!.visitingTo!!.name)
                /* if (visitorDetails!!.data.visitor != null)
                     etMobileAVDA!!.setText(visitorDetails!!.data.visitor.mobile)*/
                llRegisterBtnAVDA!!.visibility = View.GONE
                tvSaveAVDA!!.visibility = View.GONE

                etNameAVDA!!.isEnabled = false
                etVisitDateAVDA!!.isEnabled = false
                etVisitTimeAVDA!!.isEnabled = false
                etComingFromAVDA!!.isEnabled = false
                etMobileAVDA!!.isEnabled = false
                etPurposeAVDA!!.isEnabled = false

            } else {
                aTvToMeetAVDA!!.setText(visitorDetails!!.data!!.visitingTo!!.name)
                etMobileAVDA!!.setText(visitorDetails!!.data.visitor.mobile)
            }


            /*   spMeetAVDA!!.setSelection(
                   getIndex(
                       spMeetAVDA!!,
                       meet
                   )
               )*/


            if (visitorDetails.data.images != null && visitorDetails.data.images.size >0) {
                imgVwSVisitorPhotoAVDA!!.visibility = View.VISIBLE
                var path = visitorDetails.data.images.get(0).path
                path = AppConstants.IMAGE_URL + path
                Picasso.with(context)
                    .load(path)
                    .fit()
                    .into(imgVwSVisitorPhotoAVDA)
            } else if (visitorDetails.visitor != null) {
                imgVwSVisitorPhotoAVDA!!.visibility = View.VISIBLE
                var path = visitorDetails.visitor.picture
                path = AppConstants.IMAGE_URL + path
                Picasso.with(context)
                    .load(path)
                    .fit()
                    .into(imgVwSVisitorPhotoAVDA)
            } else {
                imgVwSVisitorPhotoAVDA!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
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
                        showMonth = "0" + (monthOfYear + 1)
                    } else {
                        showMonth = (monthOfYear + 1).toString()
                    }
                    etVisitDateAVDA!!.setText(year.toString() + "-" + showMonth + "-" + showDay)
                }, year, month, day

            )
            dpd.datePicker.minDate = Calendar.getInstance().timeInMillis;
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

                    val fmtOut = SimpleDateFormat("hh:mm a")

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
                    KeyboardUtility.hideKeyboard(context!!, tvSaveAVDA)
                    if (isValidation()) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a")

                        var strDate =
                            sdf.parse(etVisitDateAVDA!!.text.toString() + " " + etVisitTimeAVDA!!.text.toString())
                        if (System.currentTimeMillis() >= strDate.getTime()) {
                            Toast.makeText(
                                context,
                                "Please select time after current date and time",
                                Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                callAddVisitorDetailsApi()
                            } else {
                                SnackBar.showInternetError(context!!, snackbarView!!)
                            }
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
                    KeyboardUtility.hideKeyboard(context!!, tvRegisterAVDA)
                    if (isValidation()) {
                        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a")

                        var strDate =
                            sdf.parse(etVisitDateAVDA!!.text.toString() + " " + etVisitTimeAVDA!!.text.toString())
                        if (System.currentTimeMillis() >= strDate.getTime()) {
                            Toast.makeText(
                                context,
                                "Please select time after current date and time",
                                Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                callGetCheckMobileNoApi(mobile)
                            } else {
                                SnackBar.showInternetError(context!!, snackbarView!!)
                            }
                        }
                    }
                    //MM Close- 3/3/2021
                    //openPersionVerifyDialog()

                }
                R.id.tvDeleteAVDA -> {
                    openConfirmDeleteDialog()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openConfirmDeleteDialog() {
        try {
            var builder = AlertDialog.Builder(context!!)
            builder.setCancelable(false)
            builder.setTitle("Confirm Delete")
            builder.setMessage("Are you sure you want to delete this visitor?")
            builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog!!.dismiss()
                }
            })
            builder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                        callDeleteVisitorApi()
                    } else {
                        SnackBar.showInternetError(context!!, snackbarView!!)
                    }

                    dialog!!.dismiss()
                }
            })
            var dialog = builder.create()
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callDeleteVisitorApi() {
        var visitor_id = visitorDetails!!.id
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.deleteVisitor(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                visitor_id
            )
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                Toast.makeText(
                                    context!!,
                                    "Visitor deleted successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                            } else {
                                /*   SnackBar.showError(
                                       context!!,
                                       snackbarView!!,
                                       "Something went wrong"
                                   )*/
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
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }

    }

    lateinit var alVisitorResultData: ArrayList<CheckMobileNoResult>
    private fun callGetCheckMobileNoApi(mobile: String?) {
        try {
            var mobileData = ""
            if (mobile!!.contains("+")) {
                mobileData = mobile.substring(0, 1)
            } else {
                mobileData = mobile
            }

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.checkVisitorMobileNo(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!), mobileData!!
            )
                .enqueue(object : Callback<CheckMobileNoResponse> {

                    override fun onResponse(
                        call: Call<CheckMobileNoResponse>,
                        response: Response<CheckMobileNoResponse>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                alVisitorResultData = ArrayList()
                                alVisitorResultData.addAll(response.body().result)

                                if (alVisitorResultData.size == 0) {
                                    var visitorEntryId = visitorDetails!!.id
                                    openAddPhotoPage(visitorEntryId)
                                } else {
                                    openPersionVerifyDialog(alVisitorResultData)
                                }
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
                        call: Call<CheckMobileNoResponse>,
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

    private fun callSameVisitorAddDetailsApi(checkMobileNoResult: CheckMobileNoResult) {
        try {
/*
            var c = Calendar.getInstance().time
            System.out.println("Current time => " + c);

            var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'", Locale.getDefault())
            val timeZone: TimeZone = TimeZone.getTimeZone("IST")
            df.timeZone = timeZone

            var formattedDate: String = df.format(c)
            formattedDate = GlobalMethods.convertISTToUTCDate(formattedDate)

            var jsonObjectMain = JsonObject()

            //var jsonObjectVisitorMain = JsonObject()
            var jsonObjectVisitor = JsonObject()
            var visitor_id: String = checkMobileNoResult.id.toString()
            jsonObjectVisitor.addProperty("id", visitor_id)
            jsonObjectMain.add("visitor", jsonObjectVisitor)

            //Data Object..Start
            var jsonObjectData = JsonObject()
            jsonObjectData.addProperty("actualEntryTime", formattedDate)
            //Employee Object..Start
            var jsonObjectEmployee = JsonObject()
            jsonObjectEmployee.addProperty("name", visitorDetails!!.data!!.visitingTo!!.name)
            jsonObjectData.add("employee", jsonObjectEmployee)
            //Employee Object..End
            jsonObjectMain.add("data", jsonObjectData)
            //Data Object..End
*/

            var jsonObjectMain = JsonObject()
            var visitorID = checkMobileNoResult.id
            var visitorJsonObject = JsonObject()
            visitorJsonObject.addProperty("id", visitorID)
            jsonObjectMain.add("visitor", visitorJsonObject)

            var visitingToJsonObject = JsonObject()
            visitingToJsonObject.addProperty("id", meetToId)
            jsonObjectMain.add("visitingTo", visitingToJsonObject)

            var c = Calendar.getInstance().time
            System.out.println("Current time => " + c)
/*            var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'", Locale.getDefault())
            val timeZone: TimeZone = TimeZone.getTimeZone("IST")
            df.timeZone  = timeZone*/

            var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            var formattedActualDate: String = df.format(c)
            formattedActualDate = GlobalMethods.convertISTToUTCDate(formattedActualDate)

            var dataJsonObject = JsonObject()
            dataJsonObject.addProperty("actualEntryTime", formattedActualDate)
            dataJsonObject.addProperty("purpose", purpose)
            dataJsonObject.addProperty("comingFrom", comingFrom)
            //dataJsonObject.add("images",jsonArray)
            jsonObjectMain.add("data", dataJsonObject)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.visitorEntryRegister(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                "application/json",
                visitorDetails!!.id!!,
                jsonObjectMain
            )
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
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
                        call: Call<ResponseBody>,
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

    private fun callAddVisitorDetailsApi() {
        try {

            /*var sendUTCDate: String = ""
            val str_date = visitDate + " " + visitTime
            val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
            val input = SimpleDateFormat("yyyy-MM-dd hh:mm a")
            var formateStartdate = input.parse(str_date)
            sendUTCDate = output.format(formateStartdate)

            Log.e("utcDate", sendUTCDate)

*/
            var sendUTCDate: String = ""
            val str_date = visitDate + " " + visitTime
            val output = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val input = SimpleDateFormat("yyyy-MM-dd hh:mm a")
            var formateStartdate = input.parse(str_date)
            sendUTCDate = output.format(formateStartdate)
            sendUTCDate = GlobalMethods.convertISTToUTCDate(sendUTCDate)

            Log.e("utcDate", sendUTCDate)


            var jsonObjectMain = JsonObject()

            var jsonObjectVisitor = JsonObject()
            jsonObjectVisitor.addProperty("name", name)
            jsonObjectVisitor.addProperty("mobile", mobile)

            var jsonObjectEmployee = JsonObject()
            jsonObjectEmployee.addProperty("id", meetToId)


            var jsonObjectData = JsonObject()
            jsonObjectData.addProperty("comingFrom", comingFrom)
            jsonObjectData.addProperty("purpose", purpose)
            jsonObjectData.addProperty("expectedEntryTime", sendUTCDate)


            jsonObjectMain.add("visitor", jsonObjectVisitor)
            jsonObjectMain.add("visitingTo", jsonObjectEmployee)
            jsonObjectMain.add("data", jsonObjectData)

            Log.e("jsonObjectData", "-----==---->" + jsonObjectData)
            Log.e("jsonObjectMain", "-----==---->" + jsonObjectMain)

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
        } else if (!isValidName(name)) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter valid name.")
            etNameAVDA!!.requestFocus()
            return false
        } else if (meetToId == 0) {
            SnackBar.showError(context!!, snackbarView!!, "Please select meet person name.")
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


    private fun openPersionVerifyDialog(alVisitorResultData: ArrayList<CheckMobileNoResult>) {
        try {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_verify_visitor)
            var imgVwPhotoDVV = dialog.findViewById(R.id.imgVwPhotoDVV) as ImageView
            var tvNameDVV = dialog.findViewById(R.id.tvNameDVV) as TextView
            var tvCompanyDVV = dialog.findViewById(R.id.tvCompanyDVV) as TextView
            var tvLastVisitedDVV = dialog.findViewById(R.id.tvLastVisitedDVV) as TextView
            var tvSamePersonDVV = dialog.findViewById(R.id.tvSamePersonDVV) as TextView
            var tvChangeDVV = dialog.findViewById(R.id.tvChangeDVV) as TextView
            /*var tvYesDVV = dialog.findViewById(R.id.tvYesDVV) as TextView
            var tvNoDVV = dialog.findViewById(R.id.tvNoDVV) as TextView*/

            tvNameDVV.text = visitorDetails!!.data.visitor.name
            tvCompanyDVV.text = visitorDetails!!.data!!.comingFrom

            try {
                if (visitorDetails!!.data!!.expectedEntryTime!!.contains("T")) {
                    tvLastVisitedDVV.text =
                        GlobalMethods.convertUTCDateformate(visitorDetails!!.data!!.expectedEntryTime!!)
                } else {
                    tvLastVisitedDVV.text =
                        GlobalMethods.convertOnlyDate(visitorDetails!!.data!!.expectedEntryTime!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val transformation = RoundedTransformationBuilder()
                .cornerRadiusDp(1f)
                .oval(true)
                .build()

            if (alVisitorResultData[0].picture != null) {
                Picasso.with(context)
                    .load(AppConstants.IMAGE_URL + alVisitorResultData[0].picture)
                    .fit()
                    .transform(transformation)
                    .into(imgVwPhotoDVV)
            } else {
                imgVwPhotoDVV.setImageDrawable(resources.getDrawable(R.drawable.user))
            }

            tvSamePersonDVV.setOnClickListener {
                dialog.dismiss()
                if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                    callSameVisitorAddDetailsApi(alVisitorResultData[0])
                } else {
                    SnackBar.showInternetError(context!!, snackbarView!!)
                }
            }
            tvChangeDVV.setOnClickListener {
                var visitorEntryId = visitorDetails!!.id
                openAddPhotoPage(visitorEntryId)
            }
            /* tvYesDVV.setOnClickListener {
                 etMobileAVDA!!.setText("")
                 etMobileAVDA!!.requestFocus()
                 dialog.dismiss()
             }
             tvNoDVV.setOnClickListener {
                 dialog.dismiss()
                 finish()
             }*/
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openAddPhotoPage(visitorEntryId: Int) {

        Log.e("VisitorEntryId:Detail: ", visitorEntryId.toString())
        var intent = Intent(context!!, AddVisitorPhotoActivity::class.java)
        intent.putExtra("visitorEntryId", visitorEntryId)
        intent.putExtra("visitorName", name)
        intent.putExtra("visitingToId", meetToId)
        intent.putExtra("visitingToName", tomeet)
        intent.putExtra("expectDate", visitDate)
        intent.putExtra("expectTime", visitTime)
        intent.putExtra("purpose", purpose)
        intent.putExtra("comingFrom", comingFrom)
/*                                    intent.putExtra("company",visitorDetails!!.data!!.company!!)
                                    intent.putExtra("purpose",visitorDetails!!.data!!.purpose!!)*/
        intent.putExtra("visitorMobile", mobile)
        startActivity(intent)
    }

/*
    private fun callSameVisitorAddDetailsApi() {
        try {
            var c = Calendar.getInstance().time
            System.out.println("Current time => " + c);

            var df = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())


            var formattedDate: String = df.format(c)


            var jsonObjectMain = JsonObject()

            //var jsonObjectVisitorMain = JsonObject()
            var jsonObjectVisitor = JsonObject()
            var visitor_id: String = visitorDetails!!.id.toString()
            jsonObjectVisitor.addProperty("id", visitor_id)
            jsonObjectMain.add("visitor", jsonObjectVisitor)

            //Data Object..Start
            var jsonObjectData = JsonObject()
            jsonObjectData.addProperty("actualEntryTime", formattedDate)
            //Employee Object..Start
            var jsonObjectEmployee = JsonObject()
            jsonObjectEmployee.addProperty("name", visitorDetails!!.name)
            jsonObjectData.add("employee", jsonObjectEmployee)
            //Employee Object..End
            jsonObjectMain.add("data", jsonObjectData)
            //Data Object..End


            Log.e("jsonObjectData", "-----==---->" + jsonObjectData)
            Log.e("jsonObjectMain", "-----==---->" + jsonObjectMain)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.sameVisitorsEntryRegister(
                "application/json",
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                jsonObjectMain
            )
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                openVisitorRegisterSuccessDialog(visitorDetails!!.name!!)
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
                        call: Call<ResponseBody>,
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
*/

    private fun openVisitorRegisterSuccessDialog(name: String) {
        try {
            var dialog = Dialog(context!!)
            dialog.setContentView(R.layout.dialog_visitor_register_success)
            dialog.setCancelable(false)
            var imgVwPhotoDVRS = dialog.findViewById(R.id.imgVwPhotoDVRS) as ImageView
            var tvNameDVRS = dialog.findViewById(R.id.tvNameDVRS) as TextView
            var tvOkDVRS = dialog.findViewById(R.id.tvOkDVRS) as TextView
            tvNameDVRS.text = name
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
            spMeetAVDA = findViewById(R.id.spMeetAVDA)
            imgVwSVisitorPhotoAVDA = findViewById(R.id.imgVwSVisitorPhotoAVDA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        KeyboardUtility.hideKeyboard(context!!, imgVwBackAVDA!!)
        finish()
    }
}
