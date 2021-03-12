package com.arvi.Activity.NewApp

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.arvi.Model.NewVisitorRegisterResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
import com.arvihealthscanner.Model.GetEmployeeListResponse
import com.arvihealthscanner.Model.GetEmployeeListResult
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class AddLeaveRequestActivity : AppCompatActivity(), View.OnClickListener {
    private var alComapniesUserList: ArrayList<GetEmployeeListResult> = ArrayList()
    var imgVwBackALRA: ImageView? = null
    var spEmpNameALRA: Spinner? = null
    var etEmpIdALRA: EditText? = null
    var tvStartDateALRA: TextView? = null
    var tvEndDateALRA: TextView? = null
    var spLeaveTypeALRA: Spinner? = null
    var etLeaveReasonALRA: EditText? = null
    var tvAddRequestALRA: TextView? = null
    var tvNoEmpALRA: TextView? = null
    var sVwLeaveALRA: ScrollView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var strDateFrom: String? = null
    var strSendDateFrom: String? = null
    var strDateTo: String? = null
    var strSendDateTo: String? = null
    var strReason: String? = null
    var selectedEmpUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_leave_request)
        try {
            setIds()
            setListeners()
            callGetEmployeeListAPI()


            val leavetypeList = resources.getStringArray(R.array.default_leave_type)
            val adapterLeaveType = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item, leavetypeList
            )
            spLeaveTypeALRA!!.adapter = adapterLeaveType
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
                                            tvNoEmpALRA!!.visibility = View.GONE
                                            sVwLeaveALRA!!.visibility = View.VISIBLE
                                            etEmpIdALRA!!.setText(alComapniesUserList.get(0).employeeId)
                                            selectedEmpUserId = alComapniesUserList.get(0).id
                                            setEmpSpinnerData()

                                        } else {
                                            tvNoEmpALRA!!.visibility = View.VISIBLE
                                            sVwLeaveALRA!!.visibility = View.GONE
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
            spEmpNameALRA!!.adapter = adapter
            spEmpNameALRA!!.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    etEmpIdALRA!!.setText(alComapniesUserList.get(position).employeeId)
                    selectedEmpUserId = alComapniesUserList.get(position).id
                }
            })
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
            tvNoEmpALRA = findViewById(R.id.tvNoEmpALRA)
            sVwLeaveALRA = findViewById(R.id.sVwLeaveALRA)
            context = AddLeaveRequestActivity@ this
            snackbarView = findViewById(android.R.id.content)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgVwBackALRA -> {
                finish()
            }
            R.id.tvStartDateALRA -> {
                var from = "startDate"
                openDatePickerDialog(from)
            }
            R.id.tvEndDateALRA -> {
                var from = "endDate"
                openDatePickerDialog(from)
            }
            R.id.tvAddRequestALRA -> {
                KeyboardUtility.hideKeyboard(context!!, tvAddRequestALRA!!)
                if (isValidInput()) {
                    var formatter = SimpleDateFormat("dd/MM/yyyy")
                    var dateStart = formatter.parse(strDateFrom)
                    var dateEnd = formatter.parse(strDateTo)
                    if (dateEnd.compareTo(dateStart) < 0) {
                        SnackBar.showValidationError(
                            context!!,
                            snackbarView!!,
                            "End date must be greater than Start date"
                        )
                    } else {
                        callAddLeaveRequestApi()
                    }
                }
            }
        }
    }

    private fun callAddLeaveRequestApi() {
        try {
            var jsonObject = JsonObject()
            jsonObject.addProperty("userId", selectedEmpUserId)
            jsonObject.addProperty("dateFrom", strSendDateFrom)
            jsonObject.addProperty("dateTo", strSendDateTo)
            jsonObject.addProperty("reason", strReason)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.addLeaveRequest(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                "application/json",
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
                                dialog.setTitle("Leave request added successfully")
                                dialog.setCancelable(false)
                                dialog.setPositiveButton("Ok",
                                    object : DialogInterface.OnClickListener {
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            dialog!!.dismiss()
                                            finish()
                                        }
                                    })
                                dialog.show()
                            } else {
                                Toast.makeText(context,"No users/employees for selected dates. Cannot create reoport.",Toast.LENGTH_LONG).show()
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

    private fun isValidInput(): Boolean {
        strDateFrom = tvStartDateALRA!!.text.toString()
        strDateTo = tvEndDateALRA!!.text.toString()
        strReason = etLeaveReasonALRA!!.text.toString()
        if (strDateFrom.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please select start date")
            return false
        } else if (strDateTo.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please select end date")
            return false
        } else if (strReason.isNullOrEmpty()) {
            etLeaveReasonALRA!!.requestFocus()
            SnackBar.showValidationError(context!!, snackbarView!!, "Please enter reason for leave")
            return false
        }
        return true
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

                    var monthInt = monthOfYear + 1
                    if (monthInt < 10) {
                        showMonth = "0" + monthInt
                    } else {
                        showMonth = monthInt.toString()
                    }
                    if (from.equals("startDate")) {
                        tvStartDateALRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                        strSendDateFrom = year.toString() + "-" + showMonth + "-" + showDay
                    } else {
                        tvEndDateALRA!!.setText(showDay + "/" + showMonth + "/" + year.toString())
                        strSendDateTo = year.toString() + "-" + showMonth + "-" + showDay
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