package com.arvi.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.arvi.Adapter.GetLeaveRequestResponse
import com.arvi.Model.GetAttendanceSummaryResponse
import com.arvi.Model.GetAttendanceSummaryResponseItem
import com.arvi.Model.GetKeyMetricsResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.SnackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Dashboard_SummaryDataFragment : Fragment() {

    var tvEmpCountDSDF: TextView? = null
    var tvPresentCountDSDF: TextView? = null
    var tvAbsentCountDSDF: TextView? = null
    var tvTotalWorkHrDSDF: TextView? = null
    var tvTotalOvertimeDSDF: TextView? = null
    var tvTotalLeaveDSDF: TextView? = null
    var tvTotalLateDSDF: TextView? = null
    var tvTotalWfhDSDF: TextView? = null
    var tvTotalPendingRegularization: TextView? = null

    var appContext: Context? = null

    var startDate: String? = null
    var endDate: String? = null
    var alAttendanceSummary: ArrayList<GetAttendanceSummaryResponseItem> = ArrayList()

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
        setIds(view)
        getDefaultDates()
        callAttendanceSummaryDataApi()
        callGetKeyMetricsDataApi()
        return view
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
                                        if (response.body().workHours!=null){
                                            tvTotalWorkHrDSDF!!.setText(response.body().workHours.value.toString())
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
            tvEmpCountDSDF = view.findViewById(R.id.tvEmpCountDSDF)
            tvPresentCountDSDF = view.findViewById(R.id.tvPresentCountDSDF)
            tvAbsentCountDSDF = view.findViewById(R.id.tvAbsentCountDSDF)
            tvTotalWorkHrDSDF = view.findViewById(R.id.tvTotalWorkHrDSDF)
            tvTotalOvertimeDSDF = view.findViewById(R.id.tvTotalOvertimeDSDF)
            tvTotalLeaveDSDF = view.findViewById(R.id.tvTotalLeaveDSDF)
            tvTotalLateDSDF = view.findViewById(R.id.tvTotalLateDSDF)
            tvTotalWfhDSDF = view.findViewById(R.id.tvTotalWfhDSDF)
            tvTotalPendingRegularization = view.findViewById(R.id.tvTotalPendingRegularization)
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

                                                if(alAttendanceSummary.get(i).label.toLowerCase().equals("missed checkins")){
                                                    tvTotalPendingRegularization!!.setText(alAttendanceSummary.get(i).metric)
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
