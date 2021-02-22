package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import com.arvi.Model.GetLoginResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.KeyboardUtility
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import com.arvi.btScan.java.arvi.Settings_Activity_organised
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnterLoginDetailActivity : AppCompatActivity(), View.OnClickListener {

    var etEmpIdLDA: EditText? = null
    var etPasswordLDA: EditText? = null
    var rlLoginLDA: RelativeLayout? = null
    var context: Context? = null
    var snackbarView: View? = null

    var companyId: String? = null
    var empId: String? = null
    var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_login_detail)
        setIds()
        setListeners()
        if(intent.extras!=null){
            companyId = intent.getStringExtra("companyId")
        }
    }

    private fun setListeners() {
        rlLoginLDA!!.setOnClickListener(this)
    }

    private fun setIds() {
        context = EnterLoginDetailActivity@ this
        snackbarView = findViewById(android.R.id.content)
        etEmpIdLDA = findViewById(R.id.etEmpIdLDA)
        etPasswordLDA = findViewById(R.id.etPasswordLDA)
        rlLoginLDA = findViewById(R.id.rlLoginLDA)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.rlLoginLDA -> {
                KeyboardUtility.hideKeyboard(context!!,rlLoginLDA!!)
                if (isValidInput()) {
                    if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                        callNewLoginApi()
                    } else {
                        SnackBar.showInternetError(context!!, snackbarView!!)
                    }

                }
            }
        }
    }

    private fun callNewLoginApi() {
        try {
            var jsonObject = JsonObject()

            jsonObject.addProperty("companyId", companyId)
            jsonObject.addProperty("employeeId", empId)
            jsonObject.addProperty("password", password)



            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.getLogin(
                "application/json", jsonObject

            )

                .enqueue(object : Callback<GetLoginResponse> {

                    override fun onResponse(
                        call: Call<GetLoginResponse>,
                        response: Response<GetLoginResponse>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                SessionManager.setIsUserLoggedin(context!!,true)
                                SessionManager.setToken(context!!,response.body().accessToken)
                                SessionManager.setKioskID(context!!,response.body().user.employeeId)
                                var intent = Intent(context, DashboardActivity::class.java)
                                startActivity(intent)
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
                        call: Call<GetLoginResponse>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }

    }


    private fun isValidInput(): Boolean {
        empId = etEmpIdLDA!!.text.toString()
        password = etPasswordLDA!!.text.toString()
        if (empId.isNullOrEmpty())
        {
            SnackBar.showError(
                context!!,
                snackbarView!!,
                "Please enter Employee ID."
            )
            etEmpIdLDA!!.requestFocus()
            return false
        }else if (password.isNullOrEmpty())
        {
            SnackBar.showError(
                context!!,
                snackbarView!!,
                "Please enter password."
            )
            etPasswordLDA!!.requestFocus()
            return false
        }
        return true
    }
}