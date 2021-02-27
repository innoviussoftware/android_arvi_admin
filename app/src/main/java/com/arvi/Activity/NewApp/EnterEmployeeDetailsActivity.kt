package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.arvi.Model.GetDesignationListResponse
import com.arvi.Model.GetDesignationListResponseItem
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnterEmployeeDetailsActivity : AppCompatActivity(), View.OnClickListener {

    var imgVwBackEEDA: ImageView? = null
    var etNameEEDA: EditText? = null
    var etEmployeeIDEEDA: EditText? = null
    var etDesignationEEDA: EditText? = null
    var etMobileEEDA: EditText? = null
    var etMailEEDA: EditText? = null
    var tvTakePicEEDA: TextView? = null
    var aTvDesignationEEDA: AutoCompleteTextView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var alDesignationList: ArrayList<GetDesignationListResponseItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee_details)
        setIds()
        setListener()
        try {
            CallGetDesignationListApi()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun CallGetDesignationListApi() {

        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        MyProgressDialog.showProgressDialog(context!!)
        mAPIService!!.getDesignationList(
            AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!)
        )
            .enqueue(object : Callback<GetDesignationListResponse> {

                override fun onResponse(
                    call: Call<GetDesignationListResponse>,
                    response: Response<GetDesignationListResponse>
                ) {
                    MyProgressDialog.hideProgressDialog()
                    try {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                alDesignationList = ArrayList()
                                alDesignationList.addAll(response.body())
                                setDesignation()
                            }
                        } else if(response.code() == 401){
                            var intent = Intent(context!!, EnterCompanyDetailActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            SessionManager.clearAppSession(context!!)
                        }else {
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
                    call: Call<GetDesignationListResponse>,
                    t: Throwable
                ) {
                    MyProgressDialog.hideProgressDialog()
                }
            })

    }

    private fun setDesignation() {
        if (!alDesignationList.isNullOrEmpty()) {
            val adapter: ArrayAdapter<GetDesignationListResponseItem> =
                ArrayAdapter<GetDesignationListResponseItem>(
                    this,
                    android.R.layout.select_dialog_item,
                    alDesignationList
                )
            aTvDesignationEEDA!!.threshold = 1
            aTvDesignationEEDA!!.setAdapter(adapter)
        }
    }

    private fun setListener() {
        imgVwBackEEDA!!.setOnClickListener(this)
        tvTakePicEEDA!!.setOnClickListener(this)
    }

    private fun setIds() {
        context = EnterEmployeeDetailsActivity@ this
        snackbarView = findViewById(android.R.id.content)
        imgVwBackEEDA = findViewById(R.id.imgVwBackEEDA)
        etNameEEDA = findViewById(R.id.etNameEEDA)
        etEmployeeIDEEDA = findViewById(R.id.etEmployeeIDEEDA)
        etDesignationEEDA = findViewById(R.id.etDesignationEEDA)
        etMobileEEDA = findViewById(R.id.etMobileEEDA)
        etMailEEDA = findViewById(R.id.etMailEEDA)
        tvTakePicEEDA = findViewById(R.id.tvTakePicEEDA)
        aTvDesignationEEDA = findViewById(R.id.aTvDesignationEEDA)
    }

    override fun onBackPressed() {
        finish()
    }

    private var strEmpId: String? = null
    private var strPhone: String? = null
    var strName: String? = null

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgVwBackEEDA -> {
                finish()
            }
            R.id.tvTakePicEEDA -> {
                if (isValidInput()) {
                    var intent = Intent(context!!, TakeEmployeePicActivity::class.java)
                    intent.putExtra("name", strName)
                    intent.putExtra("mobile", strPhone)
                    intent.putExtra("employeeId", strEmpId)
                    startActivity(intent)
                }
            }
        }
    }

    private fun isValidInput(): Boolean {
        strName = etNameEEDA!!.text.toString()
        strEmpId = etEmployeeIDEEDA!!.text.toString()
        strPhone = etMobileEEDA!!.text.toString()

        if (strName!!.isEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please enter person's name")
            etNameEEDA!!.requestFocus()
            return false
        } else if (strEmpId!!.isEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please enter employee id")
            etEmployeeIDEEDA!!.requestFocus()
            return false
        } else if (strPhone!!.isEmpty()) {
            SnackBar.showValidationError(
                context!!,
                snackbarView!!,
                "Please enter person's mobile number"
            )
            etMobileEEDA!!.requestFocus()
            return false
        } else if (strPhone!!.length < 10) {
            SnackBar.showValidationError(
                context!!,
                snackbarView!!,
                "Please enter valid mobile number"
            )
            etMobileEEDA!!.requestFocus()
            return false
        }
        return true
    }

}