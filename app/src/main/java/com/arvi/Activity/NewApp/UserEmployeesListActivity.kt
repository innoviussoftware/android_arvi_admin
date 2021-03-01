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


    lateinit var alComapniesUserList:ArrayList<CompaniesUsersList>
    private fun CallGetComapniesUserListApi() {

        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        MyProgressDialog.showProgressDialog(mContext!!)
        mAPIService!!.getComaniesUsersList(
            AppConstants.BEARER_TOKEN + SessionManager.getToken(
                mContext!!
            )
        )
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    MyProgressDialog.hideProgressDialog()
                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {

                                try {
                                    val data = response.body().string()

                                    //println("Uniapp :----===-> $data")
                                    Log.e("data----==->", "" + data)
                                    alComapniesUserList = ArrayList()

                                    val jObjData = JSONObject(data)
                                    Log.e(
                                        "total data----==->",
                                        "" + jObjData.get("total").toString()
                                    )
                                    var jsonArrayMain = JSONArray(jObjData.get("result").toString())
                                    Log.e("jsonArrayMain----==->", "" + jsonArrayMain)

                                    //Log.e("jsonArrayMain----==->",""+jsonArrayMain.get(""))
                                    for (i in 0 until jsonArrayMain.length()) {
                                     //   var id = jsonArrayMain.getJSONObject(i).getString("id")
                                        var companiesUsersList = CompaniesUsersList(
                                            jsonArrayMain.getJSONObject(i).getString("id"),
                                            jsonArrayMain.getJSONObject(i).getString("employeeId"),
                                            jsonArrayMain.getJSONObject(i).getString("mobile"),
                                            jsonArrayMain.getJSONObject(i).getString("name"),
                                            jsonArrayMain.getJSONObject(i).getString("picture"),
                                            jsonArrayMain.getJSONObject(i).getString("status")
                                        )
                                        alComapniesUserList.add(companiesUsersList)
                                    }




                                    /*val gson = Gson()
                                        val responseModel = gson.fromJson(
                                            response.body().toString(),
                                            CompaniesUsersResponse::class.java
                                        )*/


                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                try {
                                    val gson = Gson()
                                    val type = object : TypeToken<CompaniesUsersResponse>() {}.type
                                    var responseData: CompaniesUsersResponse? = gson.fromJson(
                                        response.errorBody()!!.charStream(),
                                        type
                                    )
                                    Log.e("responseData----==->", "" + responseData)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                setComapniesUserData()
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
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    MyProgressDialog.hideProgressDialog()
                }
            })

    }
    
    private fun setComapniesUserData() {
        try {
            if(alComapniesUserList.size>0) {
                tvNoEmplyoeeListFound.visibility=View.GONE
                rcVwEmpliyeeListAUEL.visibility=View.VISIBLE
                var setVisitorDataAdapter = UserEmployeesListAdapter(mContext, alComapniesUserList)
                rcVwEmpliyeeListAUEL!!.layoutManager =
                    LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                rcVwEmpliyeeListAUEL!!.adapter = setVisitorDataAdapter
            }else{
                tvNoEmplyoeeListFound.visibility=View.VISIBLE
                rcVwEmpliyeeListAUEL.visibility=View.GONE
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
        var intent = Intent(mContext, EnterEmployeeDetailsActivity::class.java)
        startActivity(intent)
    }
}