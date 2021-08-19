package com.arvi.Activity.NewApp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiDynamicUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.AppConstants.BASE_Custom_URL
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnterCompanyDetailActivity : AppCompatActivity(), View.OnClickListener {



    var etCompanyNameECDA: EditText? = null
    var etAdminNameECDA: EditText? = null
    var etDesignationECDA: EditText? = null
    var etMobileNoECDA: EditText? = null
    var etMailECDA: EditText? = null
    var rlSubmitECDA: RelativeLayout? = null
    var etServerURLECDA : EditText?=null

    var context: Context? = null
    var snackbarView: View? = null
    var companyId: String? = null
    var serverUrl: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_company_detail)
        try {
            setIds()
            setListeners()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            rlSubmitECDA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = EnterCompanyDetailActivity@ this
            snackbarView = findViewById(android.R.id.content)
            etCompanyNameECDA = findViewById(R.id.etCompanyNameECDA)
            etAdminNameECDA = findViewById(R.id.etAdminNameECDA)
            etDesignationECDA = findViewById(R.id.etDesignationECDA)
            etMobileNoECDA = findViewById(R.id.etMobileNoECDA)
            etMailECDA = findViewById(R.id.etMailECDA)
            etServerURLECDA = findViewById(R.id.etServerURLECDA)
            rlSubmitECDA = findViewById(R.id.rlSubmitECDA)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View?) {
        try {
            when (view!!.id) {
                R.id.rlSubmitECDA -> {
                    if (isValidInput()) {
                        BASE_Custom_URL = serverUrl

                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                            callCheckBaseURLAPI()
                        } else {
                            SnackBar.showInternetError(context!!, snackbarView!!)
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callCheckBaseURLAPI() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiDynamicUtils.apiService
            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.checkBaseURL()
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        if (response.code() == 200) {
                            AppConstants.BASE_URL = serverUrl!!
                            SessionManager.setSelectedServerURL(
                                context!!,
                                serverUrl!!
                            )
                            var intent = Intent(context!!, EnterLoginDetailActivity::class.java)
                            intent.putExtra("companyId",companyId)
                            startActivity(intent)
                        } else {
                            SnackBar.showError(
                                context!!,
                                snackbarView!!,
                                "Please enter valid server URL"
                            )
                        }
                    }

                    override fun onFailure(
                        call: Call<ResponseBody>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        SnackBar.showError(
                            context!!,
                            snackbarView!!,
                            "Please enter valid server URL"
                        )
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            SnackBar.showError(
                context!!,
                snackbarView!!,
                "Please enter valid server URL"
            )
        }

    }


    private fun isValidInput(): Boolean {
        companyId = etCompanyNameECDA!!.text.toString()
        serverUrl = etServerURLECDA!!.text.toString()
        if (companyId.isNullOrEmpty()){
            etCompanyNameECDA!!.requestFocus()
            SnackBar.showValidationError(context!!,snackbarView!!,"enter company Id")
            return false
        }else if (serverUrl.isNullOrEmpty())
        {
            etServerURLECDA!!.requestFocus()
            SnackBar.showValidationError(context!!,snackbarView!!,"enter server url")
            return false
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
