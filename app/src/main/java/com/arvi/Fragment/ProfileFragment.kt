package com.arvi.Fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.Activity.NewApp.DashboardActivity
import com.arvi.Activity.NewApp.EnterCompanyDetailActivity
import com.arvi.Activity.NewApp.SettingActivity
import com.arvi.Activity.NewApp.UserEmployeesListActivity
import com.arvi.Model.GetCompanyProfileData
import com.arvi.Model.UserLoginResponse

import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
import com.arvihealthscanner.Model.GetEmployeeListResponse
import com.google.gson.JsonObject
import com.societyguard.Utils.FileUtil
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var mContext: Context
    var isUpdateOrNot: Boolean = false
    val MY_PERMISSIONS_REQUEST_CAMERA = 1
    var filePathProfile: String? = ""
    var fileProfile: File? = null
    var fileMaltipartProfile: MultipartBody.Part? = null
    var imgVwSettingFP: ImageView?=null
    var tvTotalEmpTitleFP:TextView?=null
    var tvTotalEmployeesFP: TextView?=null
    var etComapnyNameFP:EditText?=null
    var snackBarView:View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        try {
            mContext = requireActivity()
            snackBarView = view.findViewById(android.R.id.content)
            imgVwSettingFP = view.findViewById(R.id.imgVwSettingFP)
            tvTotalEmpTitleFP = view.findViewById(R.id.tvTotalEmpTitleFP)
            tvTotalEmployeesFP = view.findViewById(R.id.tvTotalEmployeesFP)
            etComapnyNameFP = view.findViewById(R.id.etComapnyNameFP)
            if(SessionManager.getSelectedAppMode(mContext!!).equals(mContext!!.resources.getString(R.string.admin_lite_mode))) {
                /*for admin lite mode = selfi*/
                tvTotalEmpTitleFP!!.visibility = View.VISIBLE
                tvTotalEmployeesFP!!.visibility = View.VISIBLE
            }else if(SessionManager.getSelectedAppMode(mContext!!).equals(mContext!!.resources.getString(R.string.full_mode))) {
                /*for all feature mode*/
                tvTotalEmpTitleFP!!.visibility = View.VISIBLE
                tvTotalEmployeesFP!!.visibility = View.VISIBLE
            }else if(SessionManager.getSelectedAppMode(mContext!!).equals(mContext!!.resources.getString(R.string.visitor_lite_mode))) {
                /*for visitor lite mode*/
                tvTotalEmpTitleFP!!.visibility = View.GONE
                tvTotalEmployeesFP!!.visibility = View.GONE
            }
            if(ConnectivityDetector.isConnectingToInternet(mContext)){
                callGetCompanyProfileApi()
                callGetEmployeeListApi()
            }else{
                SnackBar.showInternetError(mContext,snackBarView!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    private fun callGetEmployeeListApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(mContext!!)
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
                                        var emp_list_size = response.body().total

                                        val expireDate =
                                            "<font color=#2977DD>" + mContext.resources.getString(R.string.view_list_ttl) + "</font>"
                                        tvTotalEmployeesFP!!.text = Html.fromHtml(emp_list_size.toString() +" $expireDate")
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
                                    snackBarView!!,
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

    private fun callGetCompanyProfileApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.getCompanyProfileDetail(
                    AppConstants.BEARER_TOKEN + SessionManager.getToken(mContext),"application/json")
                    .enqueue(object : Callback<GetCompanyProfileData> {

                        override fun onResponse(
                                call: Call<GetCompanyProfileData>,
                                response: Response<GetCompanyProfileData>
                        ) {
//                        MyProgressDialog.hideProgressDialog()
                            try {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        try {
                                            if(response.body().company!=null){
                                                etComapnyNameFP!!.setText(response.body().company.name)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                } else {
                                    SnackBar.showError(
                                            context!!,
                                            snackBarView!!,
                                            "Something went wrong"
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailure(
                                call: Call<GetCompanyProfileData>,
                                t: Throwable
                        ) {
//                        MyProgressDialog.hideProgressDialog()
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try {
            mContext = requireActivity()
            setListners()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListners() {
        try {
            ivEditProfileFP.setOnClickListener(this)
            rlProfilePicFP.setOnClickListener(this)
            ivChooseProfilePicFP.setOnClickListener(this)
            tvUpdateFP.setOnClickListener(this)
            tvTotalEmployeesFP!!.setOnClickListener(this)
            ivLogoutFP.setOnClickListener(this)
            ivProfilePicFP.setImageDrawable(mContext.resources.getDrawable(R.drawable.user))
            imgVwSettingFP!!.setOnClickListener(this)
            notUpdateDetails()
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }


    override fun onClick(p0: View?) {


        var i = p0!!.id
        when (i) {
            R.id.ivLogoutFP->{
                try {
                    val builder = AlertDialog.Builder(mContext!!)
                    builder.setCancelable(false)
                    builder.setTitle(mContext.resources.getString(R.string.app_name))
                    builder.setMessage(mContext.resources.getString(R.string.logout_message))
                    builder.setIcon(mContext.resources.getDrawable(R.drawable.ic_logout))

                    builder.setPositiveButton(
                        mContext.resources.getString(R.string.logout_ttl)
                    ) { dialog, which ->

                        var intent = Intent(mContext!!, EnterCompanyDetailActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        SessionManager.clearAppSession(mContext!!)

                        dialog.dismiss()
                    }
                    builder.setNegativeButton(
                        mContext.resources.getString(R.string.cancel)
                    ) { dialog, which ->
                        dialog.dismiss()

                    }
                    val dialog = builder.create()
                    dialog.show()
                    // GlobalMethods.logOutApp(mContext)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.tvTotalEmployeesFP -> {
                try {
                    var intent=Intent(mContext, UserEmployeesListActivity::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            R.id.rlProfilePicFP -> {
                try {
                    if (checkPermission()) {
                        CropImage.startPickImageActivity(requireActivity(),this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
            R.id.ivChooseProfilePicFP -> {
                try {
                    if (checkPermission()) {
                        CropImage.startPickImageActivity(requireActivity(),this)
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            R.id.ivEditProfileFP -> {
                try {
                    if (!isUpdateOrNot) {
                        updateDetails()
                    } else {
                        notUpdateDetails()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            R.id.tvUpdateFP -> {
                notUpdateDetails()
            }
            R.id.imgVwSettingFP->{
                try {
                    openLoginDialog()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openLoginDialog() {
        try {
            var dialog = Dialog(mContext!!)
//            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_login)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            var etEmpIdDL = dialog.findViewById(R.id.etEmpIdDL) as EditText
            var etPasswordDL = dialog.findViewById(R.id.etPasswordDL) as EditText
            var rlLoginDL = dialog.findViewById(R.id.rlLoginDL) as RelativeLayout
            rlLoginDL.setOnClickListener {
              try {
                  var  emailID = etEmpIdDL!!.text.toString()
                  var password = etPasswordDL!!.text.toString()
                  if (emailID.isNullOrEmpty()) {
                      Toast.makeText(mContext,"Please enter Email Id.",Toast.LENGTH_SHORT).show()
                      etEmpIdDL!!.requestFocus()
                  } else if (!GlobalMethods.isEmailValid(emailID!!)) {
                      Toast.makeText(mContext,"Please enter valid Email Id.",Toast.LENGTH_SHORT).show()
                      etEmpIdDL!!.requestFocus()
                  } else if (password.isNullOrEmpty()) {
                      Toast.makeText(mContext,"Please enter password.",Toast.LENGTH_SHORT).show()
                      etPasswordDL!!.requestFocus()
                  }else{
                      dialog.dismiss()
                      callNewLoginApi(emailID,password)
                  }
              } catch (e: Exception) {
                  e.printStackTrace()
              }
            }

            dialog.show()
            dialog!!.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callNewLoginApi(emailID: String, password: String) {
        try {
            var jsonObject = JsonObject()

            /* jsonObject.addProperty("companyId", companyId)
             jsonObject.addProperty("employeeId", empId)
             jsonObject.addProperty("password", password)*/

            jsonObject.addProperty("username", emailID)
            jsonObject.addProperty("password", password)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(mContext!!)
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
                                    SessionManager.setKioskID(context!!, response.body().user.id.toString())
                                    /*var intent = Intent(context, DashboardActivity::class.java)
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)*/
                                    var intent = Intent(requireActivity(),SettingActivity::class.java)
                                    startActivity(intent)
                                } else if (response.code() == 401) {
                                    Toast.makeText(mContext!!,"User not found",Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(mContext!!,"Something went wrong",Toast.LENGTH_SHORT).show()
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

    private fun updateDetails() {
        try {
            isUpdateOrNot = true
            etComapnyNameFP!!.isEnabled = true
            etAddressFP.isEnabled = true
            etGSTNOFP.isEnabled = true
            etPlanTypeFP.isEnabled = true

            tvUpdateFP.visibility = View.VISIBLE
            rlProfilePicFP.visibility = View.VISIBLE
            ivEditProfileFP.visibility = View.GONE

            KeyboardUtility.showKeyboard(mContext, etComapnyNameFP!!)
/*
            val expireDate =
                "<font color=#2977DD>" + mContext.resources.getString(R.string.view_list_ttl) + "</font>"
            tvTotalEmployeesFP!!.text = Html.fromHtml("4 $expireDate")*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notUpdateDetails() {
        try {
            isUpdateOrNot = false

/*
            etAddressFP.setText()
            etGSTNOFP.setText("THD1203654789")
            etPlanTypeFP.setText("Stander")
*/

            etComapnyNameFP!!.isEnabled = false
            etAddressFP.isEnabled = false
            etGSTNOFP.isEnabled = false
            etPlanTypeFP.isEnabled = false

            tvUpdateFP.visibility = View.GONE
            rlProfilePicFP.visibility = View.GONE
            ivEditProfileFP.visibility = View.VISIBLE

            val expireDate =
                "<font color=#2977DD>" + mContext.resources.getString(R.string.view_list_ttl) + "</font>"
            tvTotalEmployeesFP!!.text = Html.fromHtml("4 $expireDate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission(): Boolean {
        try {

            val accessCamera =
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            val readPermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission =
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

            val listPermissionsNeeded = ArrayList<String>()

            if (accessCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    listPermissionsNeeded.toTypedArray(),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri).start(requireActivity(), this)
    }

    //Set image .. start
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ContextCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
/*
                                Toast.makeText(mContext, "Permission accepted.", Toast.LENGTH_LONG)
                                    .show()
*/


                                //  dialogProgress!!.dismiss()
                            }
                        }
                    }

                } else {
                    checkPermission()
                }
            }
        }

    }

    var mCropImageUri: Uri? = null

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    var imageUri: Uri? = null
                    if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                        imageUri = CropImage.getPickImageResultUri(requireActivity(), data)
                        if (CropImage.isReadExternalStoragePermissionsRequired(
                                requireActivity(),
                                imageUri
                            )
                        ) {
                            mCropImageUri = imageUri
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                                ), CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                            )
                        } else {
                            startCropImageActivity(imageUri)
                        }
                    } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                        try {
                            val uri = CropImage.getActivityResult(data).getUri()

                            //Todo: isSelectImageType=0 means user pic, isSelectImageType=1 means company pic
                            filePathProfile = FileUtil.getPath(mContext!!, uri)

                            ivProfilePicFP.setImageURI(uri)
                            /* val transformation = RoundedTransformationBuilder()
                                 .borderColor(Color.GRAY)
                                 .borderWidthDp(1f)
                                 .oval(true)
                                 .build()

                             Picasso.with(mContext)
                                 .load(uri)
                                 .fit()
                                 .transform(transformation)
                                 .into(ivProfilePicAEP)*/

                        } catch (e: Exception) {
                            e.printStackTrace();
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
