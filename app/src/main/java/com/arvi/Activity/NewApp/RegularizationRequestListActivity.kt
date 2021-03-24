package com.arvi.Activity.NewApp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetRegularizationDataAdapter
import com.arvi.Interfaces.RecyclerViewItemClicked
import com.arvi.Model.GetRegularisationRequestResponse
import com.arvi.Model.GetRegularisationRequestResponseItem
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.MyProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class RegularizationRequestListActivity : AppCompatActivity(), View.OnClickListener {


    var imgVwBackRRLA: ImageView? = null
    var rVwRequestRRLA: RecyclerView? = null
    var tvNoVisitorRRLA: TextView? = null
    var imgVwAddRequestRRLA: ImageView? = null
    var imgVwCalendarRRLA: ImageView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var startDate: String? = null
    var endDate: String? = null
    var alRegularisation: ArrayList<GetRegularisationRequestResponseItem> = ArrayList()
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regularization_request_list)
        setIds()
        setListeners()

    }

    override fun onResume() {
        super.onResume()
        getDefaultDate()
        callGetRegularizationListAPI()
    }

    private fun getDefaultDate() {
        try {
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, 0)
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            val monthFirstDay: Date = calendar.getTime()
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val monthLastDay: Date = calendar.getTime()

            val df = SimpleDateFormat("yyyy-MM-dd")
            startDate = df.format(monthFirstDay)
            endDate = df.format(monthLastDay)

            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
            Log.e("DateFirstLast", "$startDate $endDate")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun callGetRegularizationListAPI() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
           // MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.getRegularisationlist(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    context!!
                ), startDate!!
            )
                .enqueue(object : Callback<GetRegularisationRequestResponse> {

                    override fun onResponse(
                        call: Call<GetRegularisationRequestResponse>,
                        response: Response<GetRegularisationRequestResponse>
                    ) {
                    //    MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    try {
                                        alRegularisation = ArrayList()
                                        alRegularisation.addAll(response.body())
                                        setData()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                            } else {

                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<GetRegularisationRequestResponse>,
                        t: Throwable
                    ) {
                   //     MyProgressDialog.hideProgressDialog()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            if (alRegularisation != null && alRegularisation.size > 0) {

                tvNoVisitorRRLA!!.visibility = View.GONE
                rVwRequestRRLA!!.visibility = View.VISIBLE

                var listener = object : RecyclerViewItemClicked {
                    override fun onClick(view: View, position: Int) {
                        var intent = Intent(context, AddRegularizationRequestActivity::class.java)
                        intent.putExtra("requestData",alRegularisation.get(position))
                        startActivity(intent)
                    }
                }

                var setVisitorDataAdapter =
                    SetRegularizationDataAdapter(context!!, alRegularisation, listener)
                rVwRequestRRLA!!.layoutManager =
                    LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                rVwRequestRRLA!!.setAdapter(setVisitorDataAdapter)
            } else {
                tvNoVisitorRRLA!!.visibility = View.VISIBLE
                rVwRequestRRLA!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwBackRRLA!!.setOnClickListener(this)
            imgVwAddRequestRRLA!!.setOnClickListener(this)
            imgVwCalendarRRLA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = RegularizationRequestListActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackRRLA = findViewById(R.id.imgVwBackRRLA)
            rVwRequestRRLA = findViewById(R.id.rVwRequestRRLA)
            tvNoVisitorRRLA = findViewById(R.id.tvNoVisitorRRLA)
            imgVwAddRequestRRLA = findViewById(R.id.imgVwAddRequestRRLA)
            imgVwCalendarRRLA = findViewById(R.id.imgVwCalendarRRLA)
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
                R.id.imgVwBackRRLA -> {
                    finish()
                }
                R.id.imgVwAddRequestRRLA -> {
                    var intent = Intent(context, AddRegularizationRequestActivity::class.java)
                    startActivity(intent)
                }
                R.id.imgVwCalendarRRLA -> {
                    openCalendarView()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openCalendarView() {
        try {
            val datePickerDialog = DatePickerDialog(
                context!!,
                android.R.style.Theme_Holo_Dialog,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    startDate =
                        year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()
                    callGetRegularizationListAPI()
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()

            val years = datePickerDialog.findViewById<View>(
                Resources.getSystem().getIdentifier("android:id/day", null, null)
            )
            if (years != null) {
                years.visibility = View.GONE
            }
            datePickerDialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
            datePickerDialog.window!!.setGravity(Gravity.CENTER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}