package com.arvi.Fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetAllDataAdapter
import com.arvi.Adapter.SetAllDataAttendanceAdapter
import com.arvi.Model.GetGroupListResponse
import com.arvi.Model.GetGroupListResponseItem
import com.arvi.Model.GetWorkShiftListResponse
import com.arvi.Model.GetWorkShiftListResponseItem

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


class Dashboard_AllDataFragment : Fragment(), View.OnClickListener {

    var spPeriodDADF: Spinner? = null
    var spShiftDADF: Spinner? = null
    var spGroupDADF: Spinner? = null
    var rVwAllDataDADF: RecyclerView? = null

    var appContext: Context? = null
    var snackbarView: View? = null
    var startDate: String? = null
    var endDate: String? = null
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
        var view = inflater.inflate(R.layout.fragment_dashboard__all_data, container, false)
        setIds(view)
        setListeners()
        setData()
        getDefaultDates()
        callGroupListApi()
        setPeriodSpinnerData()
        callGetWorkShiftApi()
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
                spShiftDADF!!.adapter = adapter
                spShiftDADF!!.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        var strShiftName = alWorkShift.get(position).name
                        var shift_id = alGroupList.get(position).id
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
                    Log.e("Period:-",periodOption[position])
                    if(!isFirst) {
                        if (periodOption[position].equals(resources.getString(R.string.previous_month))) {
                            getPreviousMonth()

                        } else if (periodOption[position].equals(resources.getString(R.string.this_month))) {
                            getDefaultDates()

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
                spGroupDADF!!.adapter = adapter
                spGroupDADF!!.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        var strGroupName = alGroupList.get(position).name
                        var group_id = alGroupList.get(position).id
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
              var setVisitorDataAdapter = SetAllDataAdapter(appContext!!)
              rVwAllDataDADF!!.layoutManager =
                  LinearLayoutManager(appContext, LinearLayout.VERTICAL, false)
              rVwAllDataDADF!!.setAdapter(setVisitorDataAdapter)
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
        appContext = activity
        snackbarView = activity?.findViewById(android.R.id.content)

        spPeriodDADF = view.findViewById(R.id.spPeriodDADF)
        spShiftDADF = view.findViewById(R.id.spShiftDADF)
        spGroupDADF = view.findViewById(R.id.spGroupDADF)
        rVwAllDataDADF = view.findViewById(R.id.rVwAllDataDADF)


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
