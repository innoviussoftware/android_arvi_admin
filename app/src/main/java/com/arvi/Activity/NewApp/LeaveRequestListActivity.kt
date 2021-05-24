package com.arvi.Activity.NewApp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.GetLeaveRequestResponse
import com.arvi.Adapter.GetLeaveRequest_Leave
import com.arvi.Adapter.SetLeaveRequestDataAdapter
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

class LeaveRequestListActivity : AppCompatActivity(), View.OnClickListener {
    var imgVwBackLRLA: ImageView? = null
    var rVwRequestLRLA: RecyclerView? = null
    var tvNoLeaveLRLA: TextView? = null
    var imgVwAddRequestLRLA: ImageView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var alLeaveRequests: ArrayList<GetLeaveRequest_Leave> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leave_request_list)
        try {
            setIds()
            setListeners()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        try {
            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                callGetLeaveRequestApi()
            } else {
                SnackBar.showInternetError(context!!, snackbarView!!)
            }


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
                    context!!
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
            if (alLeaveRequests!=null && alLeaveRequests.size>0) {
                tvNoLeaveLRLA!!.visibility = View.GONE
                rVwRequestLRLA!!.visibility = View.VISIBLE
                var setDataAdapter = SetLeaveRequestDataAdapter(context!!, alLeaveRequests)
                rVwRequestLRLA!!.layoutManager =
                    LinearLayoutManager(context, LinearLayout.VERTICAL, false)
                rVwRequestLRLA!!.setAdapter(setDataAdapter)
            }else{
                tvNoLeaveLRLA!!.visibility = View.VISIBLE
                rVwRequestLRLA!!.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setListeners() {
        try {
            imgVwBackLRLA!!.setOnClickListener(this)
            imgVwAddRequestLRLA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = LeaveRequestListActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackLRLA = findViewById(R.id.imgVwBackLRLA)
            rVwRequestLRLA = findViewById(R.id.rVwRequestLRLA)
            tvNoLeaveLRLA = findViewById(R.id.tvNoLeaveLRLA)
            imgVwAddRequestLRLA = findViewById(R.id.imgVwAddRequestLRLA)
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
                R.id.imgVwBackLRLA -> {
                    finish()
                }
                R.id.imgVwAddRequestLRLA -> {
                    var intent = Intent(context!!, AddLeaveRequestActivity::class.java)
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}