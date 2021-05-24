package com.arvi.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AllDataAttendanceActivity
import com.arvi.Activity.NewApp.EnterCompanyDetailActivity
import com.arvi.Adapter.SetAllDataAdapter
import com.arvi.Interfaces.AttendanceItemClickListener
import com.arvi.Interfaces.RecyclerViewItemClicked
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


class Dashboard_AllDataFragment : Fragment(), View.OnClickListener {

    var spPeriodDADF: Spinner? = null
    var spShiftDADF: Spinner? = null
    var spGroupDADF: Spinner? = null
    var rVwAllDataDADF: RecyclerView? = null
    var tvNoDataDADF:TextView?=null

    var appContext: Context? = null
    var snackbarView: View? = null
    var startDate: String? = null
    var endDate: String? = null
    var alGroupList: ArrayList<GetGroupListResponseItem> = ArrayList()

    var alWorkShift: ArrayList<GetWorkShiftListResponseItem> = ArrayList()
    var isFirst: Boolean = true
    var alCalendarEvent: ArrayList<GetCalendarEventsResponseItem> = ArrayList()
    var group_id: Int = 0
    var strGroupName: String = ""

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
        var view = inflater.inflate(R.layout.fragment_dashboard__all_data, container, false)
        try {
            setIds(view)
            setListeners()
            getDefaultDates()

            if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                callCalendarEventApi()
                callGroupListApi()
                setPeriodSpinnerData()
                callGetWorkShiftApi()

            } else {
                SnackBar.showInternetError(appContext!!, snackbarView!!)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    private fun callCalendarEventApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            showProgressDialog()
            var apiCall: Call<GetCalendarEventsResponse>? = null
            if (group_id > 0) {
                apiCall = mAPIService.getCalendarEventWithGroup(
                        AppConstants.BEARER_TOKEN + SessionManager.getToken(appContext!!),
                        1,
                        startDate!!,
                        endDate!!,
                        group_id,
                        strGroupName
                )
            } else {
                apiCall = mAPIService.getCalendarEvent(
                        AppConstants.BEARER_TOKEN + SessionManager.getToken(appContext!!),
                        1,
                        startDate!!,
                        endDate!!,
                        strGroupName
                )
            }
            apiCall.enqueue(object : Callback<GetCalendarEventsResponse> {

                override fun onResponse(
                        call: Call<GetCalendarEventsResponse>,
                        response: Response<GetCalendarEventsResponse>
                ) {

                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                alCalendarEvent = ArrayList()
                                alCalendarEvent.addAll(response.body())
                                setData()
                            }
                        } else if (response.code() == 401) {
                            var intent = Intent(appContext!!, EnterCompanyDetailActivity::class.java)
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
                        call: Call<GetCalendarEventsResponse>,
                        t: Throwable
                ) {

                }
            })
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
                                                GetWorkShiftListResponseItem("", null, 0, "All", "", "")
                                        )
                                        setWorkShiftList()
                                    }
                                } else if (response.code() == 401) {
                                    var intent = Intent(appContext!!, EnterCompanyDetailActivity::class.java)
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
                spShiftDADF!!.adapter = adapter
                spShiftDADF!!.onItemSelectedListener = object :
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
            spPeriodDADF!!.adapter = adapter

            spPeriodDADF!!.onItemSelectedListener = object :
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

                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                            callCalendarEventApi()
                        } else {
                            SnackBar.showInternetError(context!!, snackbarView!!)
                        }


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
                        showToast()

                    } else {
                        dialog.dismiss()
                        if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                            callCalendarEventApi()
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
            val layout: View = inflater.inflate(R.layout.toast, requireView().findViewById(R.id.toast_layout_root) as ViewGroup?)

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
                                                GetGroupListResponseItem("", "", "", 0, 0, "All", "")
                                        )
                                        setGroupList()
                                    }
                                } else if (response.code() == 401) {
                                    var intent = Intent(appContext!!, EnterCompanyDetailActivity::class.java)
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
                spGroupDADF!!.adapter = adapter
                spGroupDADF!!.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View, position: Int, id: Long
                    ) {
                        try {
                            strGroupName = alGroupList.get(position).name
                            group_id = alGroupList.get(position).id
                            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                callCalendarEventApi()
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


    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            if (alCalendarEvent != null && alCalendarEvent.size > 0) {
                tvNoDataDADF!!.visibility = View.GONE
                rVwAllDataDADF!!.visibility = View.VISIBLE
                var listener = object : AttendanceItemClickListener {
                    override fun onClick(view: View, position: Int, from: String) {
                        var intent = Intent(appContext, AllDataAttendanceActivity::class.java)
                        intent.putExtra("groupId",group_id)
                        intent.putExtra("groupName", strGroupName)
                        intent.putExtra("alCalendarEvent", alCalendarEvent.get(position))
                        intent.putExtra("status", from)

                        startActivity(intent)
                    }
                }

                var setVisitorDataAdapter = SetAllDataAdapter(appContext!!, alCalendarEvent, listener)
                rVwAllDataDADF!!.layoutManager =
                        LinearLayoutManager(appContext, LinearLayout.VERTICAL, false)
                rVwAllDataDADF!!.setAdapter(setVisitorDataAdapter)
            }else{
                tvNoDataDADF!!.visibility = View.VISIBLE
                rVwAllDataDADF!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {

        }
    }


    private fun setIds(view: View) {
        try {
            appContext = activity
            snackbarView = activity?.findViewById(android.R.id.content)
            tvNoDataDADF = view.findViewById(R.id.tvNoDataDADF)
            spPeriodDADF = view.findViewById(R.id.spPeriodDADF)
            spShiftDADF = view.findViewById(R.id.spShiftDADF)
            spGroupDADF = view.findViewById(R.id.spGroupDADF)
            rVwAllDataDADF = view.findViewById(R.id.rVwAllDataDADF)
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
                Dashboard_AllDataFragment().apply {
                    arguments = Bundle().apply {

                    }
                }
    }
}
