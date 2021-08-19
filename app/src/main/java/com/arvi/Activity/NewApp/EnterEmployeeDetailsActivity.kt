package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Adapter.SetEmpPhotoAdapter
import com.arvi.Model.*
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
import com.arvihealthscanner.Model.GetEmployeeListResult
import com.google.gson.JsonObject
import com.societyguard.Utils.FileUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EnterEmployeeDetailsActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var strDesignation: String
    var imgVwBackEEDA: ImageView? = null
    var etNameEEDA: EditText? = null
    var etEmployeeIDEEDA: EditText? = null
    var etDesignationEEDA: EditText? = null
    var etMobileEEDA: EditText? = null
    var etMailEEDA: EditText? = null
    var tvTakePicEEDA: TextView? = null
    var spDesignationEEDA: Spinner? = null
    var spGroupEEDA: Spinner? = null
    var rVwEmployeePic: RecyclerView? = null
    var tvEditEEDA: TextView? = null
    var tvTitleEEDA: TextView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var alDesignationList: ArrayList<GetDesignationListResponseItem> = ArrayList()
    var alGroupList: ArrayList<GetGroupListResponseItem> = ArrayList()

    private var strEmpId: String? = null
    private var strPhone: String? = null
    var strName: String? = null
    var strEmail: String? = null
    var role_id: Int? = 0
    var group_id: Int? = 0
    private var strGroupName: String? = null
    var from: String? = ""
    var emp_data: GetEmployeeListResult? = null
    var alPhoto : ArrayList<GetEmpDetail_Face> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_employee_details)
        setIds()
        setListener()
        try {
            if (intent.extras != null) {
                from = intent.getStringExtra("from")
                if (from.equals("edit")) {
                    emp_data = intent.getParcelableExtra("empData")

                    rVwEmployeePic!!.visibility = View.VISIBLE
                    tvEditEEDA!!.visibility = View.VISIBLE
                    tvTitleEEDA!!.setText("Update employee details")
                    tvTakePicEEDA!!.setText("Update Pic")
                    etNameEEDA!!.setText(emp_data!!.name)
                    etEmployeeIDEEDA!!.setText(emp_data!!.employeeId)


                    etMobileEEDA!!.setText(emp_data!!.mobile)
                    etMailEEDA!!.setText(emp_data!!.email)

                    if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                        callGetUserDetailApi()
                    } else {
                        SnackBar.showInternetError(context!!, snackbarView!!)
                    }


                } else {
                    rVwEmployeePic!!.visibility = View.GONE
                    tvEditEEDA!!.visibility = View.GONE
                    tvTitleEEDA!!.setText("Add employee details")
                    tvTakePicEEDA!!.setText("Take Pic")
                }
            }


            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                CallGetDesignationListApi()
                CallGroupListApi()
            } else {
                SnackBar.showInternetError(context!!, snackbarView!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun callGetUserDetailApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.getEmpDetail(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(
                    context!!
                ),emp_data!!.id!!
            )
                .enqueue(object : Callback<GetEmpDetailResponse> {

                    override fun onResponse(
                        call: Call<GetEmpDetailResponse>,
                        response: Response<GetEmpDetailResponse>
                    ) {
//                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    try {
                                        alPhoto = ArrayList()
                                        alPhoto.addAll(response.body().faces)
                                        if (alPhoto.isNullOrEmpty() || alPhoto.size == 0){
                                            if (!emp_data!!.picture.isNullOrEmpty()){
                                                setPhoto(emp_data!!.picture)
                                            }
                                        }else{
                                            setMultiplePhoto()
                                        }
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
                        call: Call<GetEmpDetailResponse>,
                        t: Throwable
                    ) {
//                        MyProgressDialog.hideProgressDialog()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setMultiplePhoto() {
        try {

            val myList: MutableList<String> = ArrayList()
            for (i in alPhoto.indices){
                myList.add(alPhoto.get(i).data.path)
            }

            var picAdapter = SetEmpPhotoAdapter(context!!,myList)
            rVwEmployeePic!!.setAdapter(picAdapter)
            val manager =
                GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
            rVwEmployeePic!!.setLayoutManager(manager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setPhoto(picture: String?) {

        try {
            val myList: List<String> = picture!!.split(",")
            var picAdapter = SetEmpPhotoAdapter(context!!,myList)
            rVwEmployeePic!!.setAdapter(picAdapter)
            val manager =
                GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
            rVwEmployeePic!!.setLayoutManager(manager)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //private method of your class
    private fun getIndex(spinner: Spinner, myString: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(myString, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }
    private fun CallGroupListApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService

            mAPIService!!.getGroupList(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!)
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
                                    setGroupList()
                                }
                            } else if (response.code() == 401) {
                                var intent =
                                    Intent(context!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(context!!)
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
                    this,
                    android.R.layout.simple_spinner_dropdown_item, alGroupList
                )
                spGroupEEDA!!.adapter = adapter
                spGroupEEDA!!.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        strGroupName = alGroupList.get(position).name
                        group_id = alGroupList.get(position).id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }
            if (emp_data!!.group != null) {
                var group = emp_data!!.group!!.name
                spGroupEEDA!!.setSelection(
                    getIndex(
                        spGroupEEDA!!,
                        group!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun CallGetDesignationListApi() {

        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService

            mAPIService!!.getDesignationList(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!)
            )
                .enqueue(object : Callback<GetDesignationListResponse> {

                    override fun onResponse(
                        call: Call<GetDesignationListResponse>,
                        response: Response<GetDesignationListResponse>
                    ) {

                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    alDesignationList = ArrayList()
                                    alDesignationList.addAll(response.body())
                                    setDesignation()
                                }
                            } else if (response.code() == 401) {
                                var intent =
                                    Intent(context!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(context!!)
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
                        call: Call<GetDesignationListResponse>,
                        t: Throwable
                    ) {

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setDesignation() {
        try {
            if (!alDesignationList.isNullOrEmpty()) {
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item, alDesignationList
                )
                spDesignationEEDA!!.adapter = adapter
                spDesignationEEDA!!.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        strDesignation = alDesignationList.get(position).role
                        role_id = alDesignationList.get(position).id
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }
            }

            if (emp_data!!.role != null) {
                var role = emp_data!!.role!!.role
                spDesignationEEDA!!.setSelection(
                    getIndex(
                        spDesignationEEDA!!,
                        role!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListener() {
        try {
            imgVwBackEEDA!!.setOnClickListener(this)
            tvTakePicEEDA!!.setOnClickListener(this)
            tvEditEEDA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds() {
        try {
            context = EnterEmployeeDetailsActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackEEDA = findViewById(R.id.imgVwBackEEDA)
            etNameEEDA = findViewById(R.id.etNameEEDA)
            etEmployeeIDEEDA = findViewById(R.id.etEmployeeIDEEDA)
            etDesignationEEDA = findViewById(R.id.etDesignationEEDA)
            etMobileEEDA = findViewById(R.id.etMobileEEDA)
            etMailEEDA = findViewById(R.id.etMailEEDA)
            tvTakePicEEDA = findViewById(R.id.tvTakePicEEDA)
            spDesignationEEDA = findViewById(R.id.spDesignationEEDA)
            spGroupEEDA = findViewById(R.id.spGroupEEDA)
            rVwEmployeePic = findViewById(R.id.rVwEmployeePic)
            tvEditEEDA = findViewById(R.id.tvEditEEDA)
            tvTitleEEDA = findViewById(R.id.tvTitleEEDA)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        finish()
    }


    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgVwBackEEDA -> {
                finish()
            }
            R.id.tvTakePicEEDA -> {
                try {
                    KeyboardUtility.hideKeyboard(context!!,tvTakePicEEDA)
                    if (isValidInput()) {
                        if (from.equals("edit")) {
                            var intent = Intent(context!!, TakeEmployeePicActivity::class.java)
                            intent.putExtra("emp_edit_id", emp_data!!.id)
                            startActivity(intent)
                        }else{
                            var intent = Intent(context!!, TakeEmployeePicActivity::class.java)
                            intent.putExtra("name", strName)
                            intent.putExtra("mobile", strPhone)
                            intent.putExtra("employeeId", strEmpId)
                            intent.putExtra("email", strEmail)
                            intent.putExtra("role_id", role_id)
                            intent.putExtra("group_id", group_id)
                            startActivity(intent)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.tvEditEEDA -> {
                try {
                    if (isValidInput()) {
                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                            callEditUserDetailApi()
                        } else {
                            SnackBar.showInternetError(context!!, snackbarView!!)
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun callEditUserDetailApi() {
        try {
            val jsonObject = JsonObject()

            var jsonObjectRole = JsonObject()
            jsonObjectRole.addProperty("id",role_id)

            var jsonObjectGroup = JsonObject()
            jsonObjectGroup.addProperty("id",group_id)

            jsonObject.addProperty("name", strName)
            jsonObject.addProperty("email", strEmail)
            jsonObject.addProperty("mobile", strPhone)
            jsonObject.addProperty("employeeId", strEmpId)
            jsonObject.add("role",jsonObjectRole)
            jsonObject.add("group",jsonObjectGroup)
            MyProgressDialog.showProgressDialog(context!!)
            Log.e("request:",jsonObject.toString())
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            val call = mAPIService.updateEmployee(
                "application/json",
                "Bearer " + SessionManager.getToken(context!!),
                emp_data!!.id!!,
                jsonObject
            )
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    MyProgressDialog.hideProgressDialog()
                    Log.e("Upload", "success")
                    Toast.makeText(context!!,"Employee detail update successfully",Toast.LENGTH_LONG).show()
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("Upload", "failure")
                    MyProgressDialog.hideProgressDialog()
                }
            })


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }

    }

    private fun isValidInput(): Boolean {
        strName = etNameEEDA!!.text.toString()
        strEmpId = etEmployeeIDEEDA!!.text.toString()
        strPhone = etMobileEEDA!!.text.toString()
        strEmail = etMailEEDA!!.text.toString()

        if (strName!!.isEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please enter person's name")
            etNameEEDA!!.requestFocus()
            return false
        }else if(!FileUtil.isValidName(strName!!)){
            SnackBar.showError(context!!, snackbarView!!, "Please enter valid name.")
            etNameEEDA!!.requestFocus()
            return false
        }
        else if (strEmpId!!.isEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please enter employee id")
            etEmployeeIDEEDA!!.requestFocus()
            return false
        } else if (strDesignation!!.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please select role")
            return false
        } else if (strGroupName!!.isNullOrEmpty()) {
            SnackBar.showValidationError(context!!, snackbarView!!, "Please select group")
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
        } else if (strEmail.isNullOrEmpty()) {
            SnackBar.showValidationError(
                context!!,
                snackbarView!!,
                "Please enter official email address"
            )
            etMailEEDA!!.requestFocus()
            return false
        }
        return true
    }

}