package com.arvi.Fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.arvi.Activity.NewApp.EnterCompanyDetailActivity
import com.arvi.Model.*
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Dashboard_SummaryDataFragment : Fragment(), View.OnClickListener {

    var tvPresentCountDSDF: TextView? = null
    var tvAbsentCountDSDF: TextView? = null
    var tvTotalWorkHrDSDF: TextView? = null
    var tvAvgHrsDSDF: TextView? = null
    var tvTotalLateDSDF: TextView? = null
    var tvTotalPendingRegularization: TextView? = null
    var spGroupDSDF: Spinner? = null
    var spPeriodDSDF: Spinner? = null
    var spShiftDSDF: Spinner? = null
    var imgVwRefreshSummary:ImageView?=null

    var appContext: Context? = null

    var startDate: String? = null
    var endDate: String? = null
    var alAttendanceSummary: ArrayList<GetAttendanceSummaryResponseItem> = ArrayList()
    var alGroupList: ArrayList<GetGroupListResponseItem> = ArrayList()

    var alWorkShift: ArrayList<GetWorkShiftListResponseItem> = ArrayList()
    var isFirst: Boolean = true
    var group_id: Int = 0
    var snackbarView: View? = null

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
        var view = inflater.inflate(R.layout.fragment_dashboard__summary_data, container, false)
        try {
            setIds(view)
            setListeners()
            getDefaultDates()
//            showProgressDialog()
          //  callApis()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    private fun callApis() {
        try {
            if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                callAttendanceSummaryDataApi()
                callGetKeyMetricsDataApi()
                callGroupListApi()
                setPeriodSpinnerData()
                callGetWorkShiftApi()
            } else {
                SnackBar.showInternetError(appContext!!, snackbarView!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        imgVwRefreshSummary!!.setOnClickListener(this)
    }

    private fun callGetWorkShiftApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService

            mAPIService!!.getWorkShifts(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(appContext!!)
            )
                .enqueue(object : Callback<GetWorkShiftListResponse> {

                    override fun onResponse(
                        call: Call<GetWorkShiftListResponse>,
                        response: Response<GetWorkShiftListResponse>
                    ) {

                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    alWorkShift = ArrayList()
                                    alWorkShift.addAll(response.body())
                                    alWorkShift.add(
                                        0,
                                        GetWorkShiftListResponseItem("", null, 0, "All", "", "")
                                    )
                                    setWorkShiftList()
                                }
                            } else if (response.code() == 401) {
                                var intent =
                                    Intent(appContext!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(appContext!!)
                            } else {
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<GetWorkShiftListResponse>,
                        t: Throwable
                    ) {

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setWorkShiftList() {
        try {
            if (!alWorkShift.isNullOrEmpty()) {
                val adapter = ArrayAdapter(
                    appContext!!,
                    android.R.layout.simple_spinner_dropdown_item, alWorkShift
                )
                spShiftDSDF!!.adapter = adapter
                spShiftDSDF!!.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        try {
                            var strShiftName = alWorkShift.get(position).name
                            var shift_id = alGroupList.get(position).id
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setPeriodSpinnerData() {
        try {
            val periodOption = resources.getStringArray(R.array.period_array)
            val adapter = ArrayAdapter(
                appContext!!,
                android.R.layout.simple_spinner_dropdown_item, periodOption
            )
            spPeriodDSDF!!.adapter = adapter

            spPeriodDSDF!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Log.e("Period:-", periodOption[position])
                    if (!isFirst) {
                        if (periodOption[position].equals(resources.getString(R.string.previous_month))) {
                            getPreviousMonth()
                        } else if (periodOption[position].equals(resources.getString(R.string.this_month))) {
                            getDefaultDates()
                        } else {
                            getCustomDates()
                        }
                        //showProgressDialog()

                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                            callAttendanceSummaryDataApi()
                            callGetKeyMetricsDataApi()

                        } else {
                            SnackBar.showInternetError(context!!, snackbarView!!)
                        }

//                        Toast.makeText(context!!, "Work In Progress", Toast.LENGTH_LONG).show()
                    } else {
                        isFirst = false
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showProgressDialog() {
        MyProgressDialog.showProgressDialog(appContext!!)
        val delayInMillis: Long = 1000
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                MyProgressDialog.hideProgressDialog()
            }
        }, delayInMillis)
    }

    private fun getCustomDates() {
        try {
            var dialog = Dialog(appContext!!)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_select_custom_date)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            var etStartDateDSCD = dialog.findViewById(R.id.etStartDateDSCD) as EditText
            var etEndDateDSCD = dialog.findViewById(R.id.etEndDateDSCD) as EditText
            var tvOkDSCD = dialog.findViewById(R.id.tvOkDSCD) as TextView
            var tvCancelDSCD = dialog.findViewById(R.id.tvCancelDSCD) as TextView

            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, 0)
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            val monthFirstDay: Date = calendar.getTime()
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val monthLastDay: Date = calendar.getTime()

            val df = SimpleDateFormat("yyyy-MM-dd")
            startDate = df.format(monthFirstDay)
            endDate = df.format(monthLastDay)

            etStartDateDSCD.setText(startDate)
            etEndDateDSCD.setText(endDate)

            etStartDateDSCD.setOnClickListener {
                openGetDateDialog(etStartDateDSCD, "start")
            }
            etEndDateDSCD.setOnClickListener {
                openGetDateDialog(etEndDateDSCD, "end")
            }
            tvCancelDSCD.setOnClickListener {
                dialog.dismiss()
            }
            tvOkDSCD.setOnClickListener {
                try {
                    var formatter = SimpleDateFormat("yyyy-MM-dd")
                    var dateStart = formatter.parse(startDate)
                    var dateEnd = formatter.parse(endDate)
                    if (dateEnd.compareTo(dateStart) < 0) {
                        //                    SnackBar.showValidationError(appContext!!,snackbarView!!,"End date must be greater than Start date")
                        //                   Toast.makeText(appContext, "End date must be greater than Start date", Toast.LENGTH_SHORT).show()
                        showToast()

                    } else {
                        dialog.dismiss()
                        //showProgressDialog()

                        if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                            callAttendanceSummaryDataApi()
                            callGetKeyMetricsDataApi()
                        } else {
                            SnackBar.showInternetError(appContext!!, snackbarView!!)
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            dialog.show()
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showToast() {
        try {
            val inflater = layoutInflater
            val layout: View = inflater.inflate(
                R.layout.toast,
                requireView().findViewById(R.id.toast_layout_root) as ViewGroup?
            )

            val text = layout.findViewById<View>(R.id.text) as TextView
            text.text = "End date must be greater than Start date"

            val toast = Toast(appContext)
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.view = layout
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun openGetDateDialog(etStartDateDSCD: EditText, from: String) {

        val calendar: Calendar = Calendar.getInstance()
        var mYear = calendar.get(Calendar.YEAR)
        var mMonth = calendar.get(Calendar.MONTH)
        var mDay = calendar.get(Calendar.DAY_OF_MONTH)
        val strDate: String = etStartDateDSCD.text.toString()
        mYear = strDate.substring(0, 4).toInt()
        mMonth = strDate.substring(5, 7).toInt()
        mMonth = mMonth - 1
        mDay = strDate.substring(8, 10).toInt()

        val datePickerDialog = DatePickerDialog(
            appContext!!,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val mFormat = DecimalFormat("00")
                if (from.equals("start")) {
                    startDate =
                        year.toString() + "-" + mFormat.format((monthOfYear + 1).toDouble()) + "-" + mFormat.format(
                            (dayOfMonth).toDouble()
                        )
                    etStartDateDSCD.setText(startDate)
                } else {
                    endDate =
                        year.toString() + "-" + mFormat.format((monthOfYear + 1).toDouble()) + "-" + mFormat.format(
                            (dayOfMonth).toDouble()
                        )
                    etStartDateDSCD.setText(endDate)
                }
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun getPreviousMonth() {
        try {
            val calendar: Calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
            val monthFirstDay: Date = calendar.getTime()
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val monthLastDay: Date = calendar.getTime()

            val df = SimpleDateFormat("yyyy-MM-dd")
            startDate = df.format(monthFirstDay)
            endDate = df.format(monthLastDay)

            Log.e("DateFirstLast", "$startDate $endDate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callGetKeyMetricsDataApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)
            var apiCall: Call<GetKeyMetricsResponse>? = null
            if (group_id == 0) {
                apiCall = mAPIService!!.getKeyMetrics(
                    AppConstants.BEARER_TOKEN + SessionManager.getToken(
                        appContext!!
                    ), startDate!!, endDate!!
                )
            } else {
                apiCall = mAPIService!!.getKeyMetricsWithGroup(
                    AppConstants.BEARER_TOKEN + SessionManager.getToken(
                        appContext!!
                    ), startDate!!, endDate!!, group_id
                )
            }
            apiCall!!.enqueue(object : Callback<GetKeyMetricsResponse> {

                override fun onResponse(
                    call: Call<GetKeyMetricsResponse>,
                    response: Response<GetKeyMetricsResponse>
                ) {
//                        MyProgressDialog.hideProgressDialog()
                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                try {
                                    if (response.body().workHours != null) {
                                        tvTotalWorkHrDSDF!!.setText(response.body().workHours.value.toString())
                                    }

                                    if (response.body().avgHours != null) {
                                        tvAvgHrsDSDF!!.setText(response.body().avgHours.value.toString())
                                    }

                                    if (response.body().workDays != null) {

                                    }

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
                    call: Call<GetKeyMetricsResponse>,
                    t: Throwable
                ) {
//                        MyProgressDialog.hideProgressDialog()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setIds(view: View) {
        try {
            appContext = activity
            snackbarView = requireActivity().findViewById(android.R.id.content)
            tvPresentCountDSDF = view.findViewById(R.id.tvPresentCountDSDF)
            tvAbsentCountDSDF = view.findViewById(R.id.tvAbsentCountDSDF)
            tvTotalWorkHrDSDF = view.findViewById(R.id.tvTotalWorkHrDSDF)
            tvAvgHrsDSDF = view.findViewById(R.id.tvAvgHrsDSDF)
            tvTotalLateDSDF = view.findViewById(R.id.tvTotalLateDSDF)
            tvTotalPendingRegularization = view.findViewById(R.id.tvTotalPendingRegularization)
            spGroupDSDF = view.findViewById(R.id.spGroupDSDF)
            spPeriodDSDF = view.findViewById(R.id.spPeriodDSDF)
            spShiftDSDF = view.findViewById(R.id.spShiftDSDF)
            imgVwRefreshSummary = view.findViewById(R.id.imgVwRefreshSummary)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDefaultDates() {
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

            Log.e("DateFirstLast", "$startDate $endDate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callGroupListApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService

            mAPIService!!.getGroupList(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(appContext!!)
            )
                .enqueue(object : Callback<GetGroupListResponse> {

                    override fun onResponse(
                        call: Call<GetGroupListResponse>,
                        response: Response<GetGroupListResponse>
                    ) {

                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    alGroupList = ArrayList()
                                    alGroupList.addAll(response.body())
                                    alGroupList.add(
                                        0,
                                        GetGroupListResponseItem("", "", "", 0, 0, "All", "")
                                    )
                                    setGroupList()
                                }
                            } else if (response.code() == 401) {
                                var intent =
                                    Intent(appContext!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(appContext!!)
                            } else {
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<GetGroupListResponse>,
                        t: Throwable
                    ) {

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setGroupList() {
        try {
            if (!alGroupList.isNullOrEmpty()) {
                val adapter = ArrayAdapter(
                    appContext!!,
                    android.R.layout.simple_spinner_dropdown_item, alGroupList
                )
                spGroupDSDF!!.adapter = adapter
                spGroupDSDF!!.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        try {
                            var strGroupName = alGroupList.get(position).name

                            group_id = alGroupList.get(position).id
                            //    showProgressDialog()

                            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                callGetKeyMetricsDataApi()
                                callAttendanceSummaryDataApi()
                            } else {
                                SnackBar.showInternetError(context!!, snackbarView!!)
                            }


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun callAttendanceSummaryDataApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)

            var apiCall: Call<GetAttendanceSummaryResponse>? = null
            if (group_id == 0) {
                apiCall =
                    mAPIService!!.getAttendanceSummary(
                        AppConstants.BEARER_TOKEN + SessionManager.getToken(
                            appContext!!
                        ), startDate!!, endDate!!
                    )
            } else {

                apiCall = mAPIService!!.getAttendanceSummaryWithGroup(
                    AppConstants.BEARER_TOKEN + SessionManager.getToken(
                        appContext!!
                    ), startDate!!, endDate!!, group_id
                )
            }

            apiCall!!.enqueue(object : Callback<GetAttendanceSummaryResponse> {

                override fun onResponse(
                    call: Call<GetAttendanceSummaryResponse>,
                    response: Response<GetAttendanceSummaryResponse>
                ) {
//                        MyProgressDialog.hideProgressDialog()
                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                try {
                                    alAttendanceSummary = ArrayList()
                                    alAttendanceSummary.addAll(response.body())
                                    if (!alAttendanceSummary.isNullOrEmpty() && alAttendanceSummary.size > 0) {
                                        for (i in alAttendanceSummary.indices) {
                                            if (alAttendanceSummary.get(i).label.toLowerCase()
                                                    .equals("present")
                                            ) {
                                                tvPresentCountDSDF!!.setText(
                                                    alAttendanceSummary.get(
                                                        i
                                                    ).metric
                                                )
                                            }
                                            if (alAttendanceSummary.get(i).label.toLowerCase()
                                                    .equals("absent")
                                            ) {
                                                tvAbsentCountDSDF!!.setText(
                                                    alAttendanceSummary.get(
                                                        i
                                                    ).metric
                                                )
                                            }
                                            if (alAttendanceSummary.get(i).label.toLowerCase()
                                                    .equals("late checkin")
                                            ) {
                                                tvTotalLateDSDF!!.setText(
                                                    alAttendanceSummary.get(
                                                        i
                                                    ).metric
                                                )
                                            }

                                            if (alAttendanceSummary.get(i).label.toLowerCase()
                                                    .equals("missed checkins")
                                            ) {
                                                tvTotalPendingRegularization!!.setText(
                                                    alAttendanceSummary.get(i).metric
                                                )
                                            }
                                        }
                                    }
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
                    call: Call<GetAttendanceSummaryResponse>,
                    t: Throwable
                ) {
//                        MyProgressDialog.hideProgressDialog()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Dashboard_SummaryDataFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.imgVwRefreshSummary->{
                callApis()
                showProgressDialog()
            }
        }
    }
}
