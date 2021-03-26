package com.arvi.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.arvi.Model.*
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Dashboard_SummaryDataFragment : Fragment() {

    var tvPresentCountDSDF: TextView? = null
    var tvAbsentCountDSDF: TextView? = null
    var tvTotalWorkHrDSDF: TextView? = null
    var tvAvgHrsDSDF: TextView? = null
    var tvTotalLateDSDF: TextView? = null
    var tvTotalPendingRegularization: TextView? = null
    var spGroupDSDF: Spinner? = null
    var spPeriodDSDF: Spinner? = null
    var spShiftDSDF: Spinner? = null

    var appContext: Context? = null

    var startDate: String? = null
    var endDate: String? = null
    var alAttendanceSummary: ArrayList<GetAttendanceSummaryResponseItem> = ArrayList()
    var alGroupList: ArrayList<GetGroupListResponseItem> = ArrayList()

    var alWorkShift : ArrayList<GetWorkShiftListResponseItem> = ArrayList()
    var isFirst :Boolean= true


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
            getDefaultDates()
            callAttendanceSummaryDataApi()
            callGetKeyMetricsDataApi()
            callGroupListApi()
            setPeriodSpinnerData()
            callGetWorkShiftApi()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
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
                                    alWorkShift.add(0,
                                        GetWorkShiftListResponseItem("",null,0,"All","","")
                                    )
                                    setWorkShiftList()
                                }
                            } else if (response.code() == 401) {

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
                    Log.e("Period:-",periodOption[position])
                    if(!isFirst) {
                        if (periodOption[position].equals(resources.getString(R.string.previous_month))) {
                            getPreviousMonth()
                            callAttendanceSummaryDataApi()
                            callGetKeyMetricsDataApi()
                        } else if (periodOption[position].equals(resources.getString(R.string.this_month))) {
                            getDefaultDates()
                            callAttendanceSummaryDataApi()
                            callGetKeyMetricsDataApi()
                        } else {

                        }
                        Toast.makeText(context!!, "Work In Progress", Toast.LENGTH_LONG).show()
                    }else{
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
            mAPIService!!.getKeyMetrics(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    appContext!!
                ), startDate!!, endDate!!
            )
                .enqueue(object : Callback<GetKeyMetricsResponse> {

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
            tvPresentCountDSDF = view.findViewById(R.id.tvPresentCountDSDF)
            tvAbsentCountDSDF = view.findViewById(R.id.tvAbsentCountDSDF)
            tvTotalWorkHrDSDF = view.findViewById(R.id.tvTotalWorkHrDSDF)
            tvAvgHrsDSDF = view.findViewById(R.id.tvAvgHrsDSDF)
            tvTotalLateDSDF = view.findViewById(R.id.tvTotalLateDSDF)
            tvTotalPendingRegularization = view.findViewById(R.id.tvTotalPendingRegularization)
            spGroupDSDF = view.findViewById(R.id.spGroupDSDF)
            spPeriodDSDF = view.findViewById(R.id.spPeriodDSDF)
            spShiftDSDF = view.findViewById(R.id.spShiftDSDF)
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
                                    alGroupList.add(0,
                                        GetGroupListResponseItem("","","",0,0,"All","")
                                    )
                                    setGroupList()
                                }
                            } else if (response.code() == 401) {

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
                            var group_id = alGroupList.get(position).id
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
            mAPIService!!.getAttendanceSummary(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    appContext!!
                ), startDate!!, endDate!!
            )
                .enqueue(object : Callback<GetAttendanceSummaryResponse> {

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
}
