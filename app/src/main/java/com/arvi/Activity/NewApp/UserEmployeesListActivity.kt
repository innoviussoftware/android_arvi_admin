package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.UserEmployeesListAdapter
import com.arvi.Model.CompaniesUsersList
import com.arvi.Model.CompaniesUsersResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import com.arvihealthscanner.Model.GetEmployeeListResponse
import com.arvihealthscanner.Model.GetEmployeeListResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_user_employees_list.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserEmployeesListActivity : AppCompatActivity() {

    lateinit var mContext: Context
    var snackbarView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_employees_list)
        mContext = this@UserEmployeesListActivity
        snackbarView = findViewById(android.R.id.content)
        try {
            CallGetComapniesUserListApi()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    lateinit var alComapniesUserList: ArrayList<GetEmployeeListResult>
    private fun CallGetComapniesUserListApi() {

        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(mContext!!)
            mAPIService!!.getComaniesUsersList(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    mContext!!
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
                                        setComapniesUserData()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            } else if (response.code() == 401) {
                                var intent = Intent(mContext!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(mContext!!)
                            } else {
                                SnackBar.showError(
                                    mContext!!,
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

    private fun setComapniesUserData() {
        try {
            if (alComapniesUserList.size > 0) {
                tvNoEmplyoeeListFound.visibility = View.GONE
                rcVwEmpliyeeListAUEL.visibility = View.VISIBLE
                var setVisitorDataAdapter = UserEmployeesListAdapter(mContext, alComapniesUserList)
                rcVwEmpliyeeListAUEL!!.layoutManager =
                    LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                rcVwEmpliyeeListAUEL!!.adapter = setVisitorDataAdapter
            } else {
                tvNoEmplyoeeListFound.visibility = View.VISIBLE
                rcVwEmpliyeeListAUEL.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //OnClicked Listners

    fun backEmpolyeeList(view: View) {
        finish()
    }

    fun addEmployeeDetails(view: View) {
        try {
            var intent = Intent(mContext, EnterEmployeeDetailsActivity::class.java)
            intent.putExtra("from","add")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}