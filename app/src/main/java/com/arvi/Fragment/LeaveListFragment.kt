package com.arvi.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddLeaveRequestActivity
import com.arvi.Adapter.GetLeaveRequestResponse
import com.arvi.Adapter.GetLeaveRequest_Leave
import com.arvi.Adapter.SetLeaveRequestDataAdapter
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.SnackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LeaveListFragment : Fragment(), View.OnClickListener {

    var imgVwBackLRLA: ImageView? = null
    var rVwRequestLRLA: RecyclerView? = null
    var tvNoLeaveLRLA: TextView? = null
    var imgVwAddRequestLRLA: ImageView? = null
    var imgVwCalendarLLF: ImageView? = null

    var appContext: Context? = null
    var snackbarView: View? = null
    var alLeaveRequests: ArrayList<GetLeaveRequest_Leave> = ArrayList()
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    var startDate: String? = null
    var endDate: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_leave_list, container, false)
        appContext = activity
        snackbarView = view.findViewById(android.R.id.content)
        imgVwBackLRLA = view.findViewById(R.id.imgVwBackLRLA)
        rVwRequestLRLA = view.findViewById(R.id.rVwRequestLRLA)
        tvNoLeaveLRLA = view.findViewById(R.id.tvNoLeaveLRLA)
        imgVwAddRequestLRLA = view.findViewById(R.id.imgVwAddRequestLRLA)
        imgVwCalendarLLF = view.findViewById(R.id.imgVwCalendarLLF)
        imgVwBackLRLA!!.setOnClickListener(this)
        imgVwAddRequestLRLA!!.setOnClickListener(this)
        imgVwCalendarLLF!!.setOnClickListener(this)

        /*       if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                   callGetLeaveRequestApi()
               } else {
                   SnackBar.showInternetError(appContext!!, snackbarView!!)
               }
       */
        return view
    }


    override fun onResume() {
        super.onResume()
        try {
            getDefaultDate()
            if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                callGetLeaveRequestApi()
            } else {
                SnackBar.showInternetError(appContext!!, snackbarView!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    private fun callGetLeaveRequestApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.getLeaveRequest(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    appContext!!
                )
            )
                .enqueue(object : Callback<GetLeaveRequestResponse> {

                    override fun onResponse(
                        call: Call<GetLeaveRequestResponse>,
                        response: Response<GetLeaveRequestResponse>
                    ) {
//                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    try {
                                        alLeaveRequests = ArrayList()
                                        alLeaveRequests.addAll(response.body().leaves!!)
                                        setLeaveRequestData()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
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
                        call: Call<GetLeaveRequestResponse>,
                        t: Throwable
                    ) {
//                        MyProgressDialog.hideProgressDialog()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("WrongConstant")
    private fun setLeaveRequestData() {
        try {
            if (alLeaveRequests != null && alLeaveRequests.size > 0) {
                tvNoLeaveLRLA!!.visibility = View.GONE
                rVwRequestLRLA!!.visibility = View.VISIBLE
                var setDataAdapter = SetLeaveRequestDataAdapter(appContext!!, alLeaveRequests)
                rVwRequestLRLA!!.layoutManager =
                    LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                rVwRequestLRLA!!.setAdapter(setDataAdapter)
            } else {
                tvNoLeaveLRLA!!.visibility = View.VISIBLE
                rVwRequestLRLA!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            LeaveListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onClick(viewq: View?) {
        try {
            when (viewq!!.id) {

                R.id.imgVwAddRequestLRLA -> {
                    var intent = Intent(appContext!!, AddLeaveRequestActivity::class.java)
                    startActivity(intent)
                }
                R.id.imgVwCalendarLLF->{
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
                appContext!!,
                android.R.style.Theme_Holo_Dialog,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in textbox
                    startDate =
                        year.toString() + "-" + (monthOfYear + 1).toString() + "-" + dayOfMonth.toString()

                    /*                if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                                        callGetRegularizationListAPI()
                                    } else {
                                        SnackBar.showInternetError(appContext!!, snackbarView!!)
                                    }
                */

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
            datePickerDialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
            datePickerDialog.window!!.setGravity(Gravity.CENTER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}