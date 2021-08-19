package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import com.arvi.Model.GetLoginResponse
import com.arvi.Model.UserLoginResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
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
    var emailID: String? = null
    var password: String? = null

    lateinit var ivPassWordShowAELD: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_login_detail)
        try {
            setIds()
            setListeners()
            if (intent.extras != null) {
                companyId = intent.getStringExtra("companyId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            rlLoginLDA!!.setOnClickListener(this)
            ivPassWordShowAELD!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = EnterLoginDetailActivity@ this
            snackbarView = findViewById(android.R.id.content)
            etEmpIdLDA = findViewById(R.id.etEmpIdLDA)
            etPasswordLDA = findViewById(R.id.etPasswordLDA)
            rlLoginLDA = findViewById(R.id.rlLoginLDA)
            ivPassWordShowAELD = findViewById(R.id.ivPassWordShowAELD)
            ivPassWordShowAELD.setImageResource(R.drawable.ic_pw_dont_show_blk)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    var flag_pwd = 0

    override fun onClick(view: View?) {
        try {
            when (view!!.id) {
                R.id.rlLoginLDA -> {
                    KeyboardUtility.hideKeyboard(context!!, rlLoginLDA!!)
                    if (isValidInput()) {
                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                            callNewLoginApi()
                        } else {
                            SnackBar.showInternetError(context!!, snackbarView!!)
                        }

                    }
                }

                R.id.ivPassWordShowAELD -> {
                    try {
                        if (flag_pwd == 1) {
                            //show password
                            etPasswordLDA!!.transformationMethod =
                                PasswordTransformationMethod.getInstance()
                            ivPassWordShowAELD.setImageResource(R.drawable.ic_pw_dont_show_blk)
                            flag_pwd = 0
                        } else {
                            //hide password
                            etPasswordLDA!!.transformationMethod =
                                HideReturnsTransformationMethod.getInstance()
                            ivPassWordShowAELD.setImageResource(R.drawable.ic_pw_show_blk)
                            flag_pwd = 1
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callNewLoginApi() {
        try {
            var jsonObject = JsonObject()

            /* jsonObject.addProperty("companyId", companyId)
             jsonObject.addProperty("employeeId", empId)
             jsonObject.addProperty("password", password)*/

            jsonObject.addProperty("username", emailID)
            jsonObject.addProperty("password", password)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.setUserLogin("application/json", jsonObject)

                .enqueue(object : Callback<UserLoginResponse> {

                    override fun onResponse(
                        call: Call<UserLoginResponse>,
                        response: Response<UserLoginResponse>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                SessionManager.setIsUserLoggedin(context!!, true)
                                SessionManager.setToken(context!!, response.body().accessToken)

                                //MM Changed: 06/04/2021 After Login Flow changed id to emailID
                                SessionManager.setKioskID(context!!, response.body().user.id.toString())
//                                var intent = Intent(context, DashboardActivity::class.java)
                                var intent = Intent(context, SelfiCheckInActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                            } else if (response.code() == 401) {
                                SnackBar.showError(
                                    context!!,
                                    snackbarView!!,
                                    "User not found."
                                )
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
                        call: Call<UserLoginResponse>,
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
        emailID = etEmpIdLDA!!.text.toString()
        // empId = etEmpIdLDA!!.text.toString()
        password = etPasswordLDA!!.text.toString()
        if (emailID.isNullOrEmpty()) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter Email Id.")
            etEmpIdLDA!!.requestFocus()
            return false
        } else if (!GlobalMethods.isEmailValid(emailID!!)) {
            SnackBar.showError(context!!, snackbarView!!, "Please enter valid Email Id.")
            etEmpIdLDA!!.requestFocus()
            return false
        } else if (password.isNullOrEmpty()) {
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