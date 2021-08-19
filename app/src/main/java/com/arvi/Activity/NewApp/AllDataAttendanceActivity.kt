package com.arvi.Activity.NewApp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetAllDataAttendanceAdapter
import com.arvi.Model.GetCalendarEventsResponseItem
import com.arvi.Model.GetEmpDayDetailResponse
import com.arvi.Model.GetEmpDayDetailResponseItem
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AllDataAttendanceActivity : AppCompatActivity(), View.OnClickListener {
    var imgVwBackADAA: ImageView? = null
    var tvNoDataADAA:TextView?=null
    var tvDetailADAA: TextView? = null
    var rVwDataADAA: RecyclerView? = null
    var etEmpNameADAA: EditText? = null
    var context: Context? = null
    var snackbarView: View? = null

    var alCalendarEvent: GetCalendarEventsResponseItem?=null
    var groupName:String?=""
    var status:String?="" //from = Absent,present,leave,miss,visitor,holiday
    var groupId:Int=0

    var alDetails :ArrayList<GetEmpDayDetailResponseItem> = ArrayList()
    var arraylist :ArrayList<GetEmpDayDetailResponseItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_data_attendance)
        try {
            setIds()
            setListeners()
            if(intent.extras!=null){
                groupId = intent.getIntExtra("groupId",0)
                alCalendarEvent = intent.getParcelableExtra("alCalendarEvent")
                groupName = intent.getStringExtra("groupName")
                status = intent.getStringExtra("status")

                val outputDate = SimpleDateFormat("dd MMM, yyyy")
                val input = SimpleDateFormat("yyyy-MM-dd")
                var formateStartdate = input.parse(alCalendarEvent!!.date)
                var showDate = outputDate.format(formateStartdate)

                tvDetailADAA!!.setText("Date: "+showDate+", Group: "+groupName)
            }
            if(ConnectivityDetector.isConnectingToInternet(context!!)){
                callGetEmpDetailByDayApi()
            }else{
                SnackBar.showInternetError(context!!,snackbarView!!)
            }
            etEmpNameADAA!!.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(arg0: Editable) {
                }

                override fun beforeTextChanged(
                        arg0: CharSequence, arg1: Int,
                        arg2: Int, arg3: Int
                ) {

                }

                override fun onTextChanged(
                        arg0: CharSequence, arg1: Int, arg2: Int,
                        arg3: Int
                ) {
                    filter(arg0.toString())
                    // TODO Auto-generated method stub
                }
            })



//            setData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filter(charText: String) {
        var charText = charText
        try {
            charText = charText.toLowerCase(Locale.getDefault())
            alDetails.clear()
            if (charText.length == 0) {
                alDetails.addAll(arraylist)
            } else {
                for (wp in arraylist) {
                    if (wp.emp_name!!.toLowerCase(Locale.getDefault()).contains(charText)) {
                        alDetails.add(wp)
                    } else if (wp.emp_id != null && wp.emp_id.toLowerCase(Locale.getDefault()).contains(charText)) {
                        alDetails.add(wp)
                    }
                }
            }
            setData()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun callGetEmpDetailByDayApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)
            var apiCall: Call<GetEmpDayDetailResponse>? = null

            apiCall = mAPIService.getEmpDayDetail(
                    AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                    alCalendarEvent!!.date!!,
                    /*groupId,*/
                    groupName!!,
                    status!!
            )

            apiCall.enqueue(object : Callback<GetEmpDayDetailResponse> {

                override fun onResponse(
                        call: Call<GetEmpDayDetailResponse>,
                        response: Response<GetEmpDayDetailResponse>
                ) {

                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                alDetails = ArrayList()
                                arraylist = ArrayList()
                                alDetails.addAll(response.body())
                                arraylist.addAll(response.body())
                                setData()
                            }
                        } else if (response.code() == 401) {
                            var intent = Intent(context!!, EnterCompanyDetailActivity::class.java)
                            intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            SessionManager.clearAppSession(context!!)
                        } else {
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }

                override fun onFailure(
                        call: Call<GetEmpDayDetailResponse>,
                        t: Throwable
                ) {
                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setListeners() {
        try {
            imgVwBackADAA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    private fun setData() {
        try {
            if(!alDetails.isNullOrEmpty() && alDetails.size>0) {
                tvNoDataADAA!!.visibility=View.GONE
                rVwDataADAA!!.visibility=View.VISIBLE
                var setVisitorDataAdapter = SetAllDataAttendanceAdapter(context!!,alDetails)
                rVwDataADAA!!.layoutManager =
                        LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                rVwDataADAA!!.setAdapter(setVisitorDataAdapter)
            }else{
                tvNoDataADAA!!.visibility=View.VISIBLE
                rVwDataADAA!!.visibility=View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = AllDataAttendanceActivity@ this
            snackbarView = findViewById(android.R.id.content)
            rVwDataADAA = findViewById(R.id.rVwDataADAA)
            tvNoDataADAA = findViewById(R.id.tvNoDataADAA)
            imgVwBackADAA = findViewById(R.id.imgVwBackADAA)
            tvDetailADAA = findViewById(R.id.tvDetailADAA)
            etEmpNameADAA = findViewById(R.id.etEmpNameADAA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.imgVwBackADAA -> {
                    finish()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}