package com.arvi.Activity.NewApp

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.arvi.Model.UserLoginResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiDynamicUtils
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.SessionManager.SessionManager.setDefaultFaceWidth
import com.arvi.SessionManager.SessionManager.setDetectFaceWidth
import com.arvi.Utils.*
import com.arvi.Utils.AppConstants.BASE_Custom_URL
import com.arvi.Utils.AppConstants.BASE_URL
import com.arvi.btScan.java.arvi.Config
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SettingActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var screenOption: Array<String>
    var imgVwBackSA: ImageView? = null
    var spServerUrlSA: Spinner? = null
    var etServerUrlSA: EditText? = null

    var spScreensSA: Spinner? = null
    var spCameraFacingSA: Spinner? = null

    var spAppModeSA: Spinner? = null

    var rgGPSTrackSA: RadioGroup? = null
    var rbYesTrackSA: RadioButton? = null
    var rbNoTrackSA: RadioButton? = null
    var llGpsSA: LinearLayout? = null
    var llLaunchOptionSA: LinearLayout? = null

    var etDefaultMinWidthSA: EditText? = null
    var etDetectMinFaceWidthSA: EditText? = null

    var tvSaveSA: TextView? = null

    var context: Context? = null
    var snackbarView: View? = null
    var strSelectedServerOption: String = ""
    var strSelectedCameraFacing: String = ""
    var strSelectedScreenOption: String = ""
    var strSelectedGPSOption: String = ""
    var strSelectedAppModeOption: String = ""

    var etRestartAppSA: EditText? = null
    var strRestartAt :String = "12"

    var oldUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        try {
            setIDs()
            setListeners()
            oldUrl = SessionManager.getSelectedServerURL(context!!)

            strSelectedAppModeOption = SessionManager.getSelectedAppMode(context!!)!!
            if (SessionManager.getSelectedAppMode(context!!)
                    .equals(context!!.resources.getString(R.string.admin_lite_mode))
            ) {
                /*for admin lite mode = selfi*/
                llGpsSA!!.visibility = View.VISIBLE
                llLaunchOptionSA!!.visibility = View.VISIBLE
            } else if (SessionManager.getSelectedAppMode(context!!)
                    .equals(context!!.resources.getString(R.string.full_mode))
            ) {
                /*for all feature mode*/
                llGpsSA!!.visibility = View.VISIBLE
                llLaunchOptionSA!!.visibility = View.VISIBLE
            } else if (SessionManager.getSelectedAppMode(context!!)
                    .equals(context!!.resources.getString(R.string.visitor_lite_mode))
            ) {
                /*for visitor lite mode*/
                llGpsSA!!.visibility = View.GONE
                llLaunchOptionSA!!.visibility = View.VISIBLE
            }
            strSelectedServerOption = SessionManager.getSelectedServerURL(context!!)
            strSelectedCameraFacing = SessionManager.getSelectedCameraFacing(context!!)
            strSelectedScreenOption = SessionManager.getSelectedDefaultScreen(context!!)
            strSelectedGPSOption = SessionManager.getSelectedGPSOption(context!!)
            strSelectedAppModeOption = SessionManager.getSelectedAppMode(context!!)!!

            if (!strSelectedServerOption.isNullOrEmpty()) {
                etServerUrlSA!!.setText(strSelectedServerOption)
            }

            if (!strSelectedGPSOption.isNullOrEmpty()) {
                if (strSelectedGPSOption.equals("Yes")) {
                    rbYesTrackSA!!.isChecked = true
                    rbNoTrackSA!!.isChecked = false
                } else {
                    rbYesTrackSA!!.isChecked = false
                    rbNoTrackSA!!.isChecked = true
                }
            } else {
                rbYesTrackSA!!.isChecked = true
                rbNoTrackSA!!.isChecked = false
            }

            //setDefaultServerURLSpinnerData()
            setDefaultScreenSpinnerData()
            setDefaultCameraFacingSpinnerData()
            setDefaultAppModeSpinnerData()
            Config.defaultMinFaceWidth = SessionManager.getDefaultFaceWidth(context!!)
            Config.detectMinFaceWidth = SessionManager.getDetectFaceWidth(context!!)

            etDefaultMinWidthSA!!.setText(Config.defaultMinFaceWidth.toString())
            etDetectMinFaceWidthSA!!.setText(Config.detectMinFaceWidth.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultAppModeSpinnerData() {
        try {
            var screenOption: Array<out String>? = emptyArray()
            screenOption = resources.getStringArray(R.array.default_app_mode)

            val adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, screenOption)
            spAppModeSA!!.adapter = adapter

            spAppModeSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    try {
                        strSelectedAppModeOption = screenOption[position]
                        Log.e("SelectedAppModeOption", strSelectedAppModeOption)
                        setDefaultScreenSpinnerData()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            if (!strSelectedAppModeOption.isNullOrEmpty()) {
                try {
                    selectSpinnerValue(spAppModeSA!!, strSelectedAppModeOption!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun selectSpinnerValue(spinner: Spinner, myString: String) {
        try {
            for (i in 0 until spinner.count) {
                if (spinner.getItemAtPosition(i).toString() == myString) {
                    spinner.setSelection(i)
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun setListeners() {
        try {
            imgVwBackSA!!.setOnClickListener(this)
            tvSaveSA!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setIDs() {
        try {
            context = SettingActivity@ this
            snackbarView = findViewById(android.R.id.content)
            imgVwBackSA = findViewById(R.id.imgVwBackSA)
            etServerUrlSA = findViewById(R.id.etServerUrlSA)

            llGpsSA = findViewById(R.id.llGpsSA)
            llLaunchOptionSA = findViewById(R.id.llLaunchOptionSA)
            spServerUrlSA = findViewById(R.id.spServerUrlSA)
            spScreensSA = findViewById(R.id.spScreensSA)
            spCameraFacingSA = findViewById(R.id.spCameraFacingSA)
            spAppModeSA = findViewById(R.id.spAppModeSA)
            rgGPSTrackSA = findViewById(R.id.rgGPSTrackSA)
            rbYesTrackSA = findViewById(R.id.rbYesTrackSA)
            rbNoTrackSA = findViewById(R.id.rbNoTrackSA)

            etDefaultMinWidthSA = findViewById(R.id.etDefaultMinWidthSA)
            etDetectMinFaceWidthSA = findViewById(R.id.etDetectMinFaceWidthSA)

            etRestartAppSA = findViewById(R.id.etRestartAppSA)
            tvSaveSA = findViewById(R.id.tvSaveSA)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDefaultCameraFacingSpinnerData() {
        try {
            val facingOption = resources.getStringArray(R.array.camera_facing_option)
            val adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, facingOption)
            spCameraFacingSA!!.adapter = adapter

            spCameraFacingSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    try {
                        strSelectedCameraFacing = facingOption[position]
                        Log.e("SelectedCameraFacing", strSelectedCameraFacing)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            if (!strSelectedCameraFacing.isNullOrEmpty()) {
                try {
                    selectSpinnerValue(spCameraFacingSA!!, strSelectedCameraFacing!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setDefaultScreenSpinnerData() {
        try {


            if (SessionManager.getSelectedAppMode(context!!)
                    .equals(context!!.resources.getString(R.string.admin_lite_mode)) || strSelectedAppModeOption.equals(
                    context!!.resources.getString(R.string.admin_lite_mode)
                )
            ) {
                /*for admin lite mode = selfi*/
                screenOption = resources.getStringArray(R.array.default_screen_admin_lite)
            } else if (SessionManager.getSelectedAppMode(context!!)
                    .equals(context!!.resources.getString(R.string.full_mode)) || strSelectedAppModeOption.equals(
                    context!!.resources.getString(R.string.full_mode)
                )
            ) {
                /*for all feature mode*/
                screenOption = resources.getStringArray(R.array.default_screen_option)
            } else if (SessionManager.getSelectedAppMode(context!!)
                    .equals(context!!.resources.getString(R.string.visitor_lite_mode)) || strSelectedAppModeOption.equals(
                    context!!.resources.getString(R.string.visitor_lite_mode)
                )
            ) {
                /*for visitor lite mode*/
                screenOption = resources.getStringArray(R.array.default_screen_visitor_mode)
            }

            val adapter =
                ArrayAdapter(
                    context!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    screenOption!!
                )
            spScreensSA!!.adapter = adapter

            spScreensSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {

                    try {
                        strSelectedScreenOption = screenOption[position]
                        Log.e("SelectedScreenOption", strSelectedScreenOption)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

            if (!strSelectedScreenOption.isNullOrEmpty()) {
                try {
                    selectSpinnerValue(spScreensSA!!, strSelectedScreenOption!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

/*
    private fun setDefaultServerURLSpinnerData() {
        try {
            val serverOption = resources.getStringArray(R.array.default_server_url)
            val adapter =
                ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, serverOption)
            spServerUrlSA!!.adapter = adapter

            spServerUrlSA!!.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    strSelectedServerOption = serverOption[position]
                    Log.e("SelectedServerOption", strSelectedServerOption)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/


    override fun onBackPressed() {
        finish()
    }

    override fun onClick(view: View?) {
        try {
            when (view!!.id) {
                R.id.imgVwBackSA -> {
                    try {
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                R.id.tvSaveSA -> {
                    try {
                        KeyboardUtility.hideKeyboard(context!!, tvSaveSA)
                        strSelectedServerOption = etServerUrlSA!!.text.toString()
                        if (strSelectedServerOption.isNullOrEmpty()) {
                            SnackBar.showValidationError(
                                context!!,
                                snackbarView!!,
                                "please enter your server url"
                            )
                            etServerUrlSA!!.requestFocus()
                        } else if (etDefaultMinWidthSA!!.text.toString().isNullOrEmpty()) {
                            SnackBar.showValidationError(
                                context!!,
                                snackbarView!!,
                                "Please enter default minimum face width"
                            )
                            etDefaultMinWidthSA!!.requestFocus()
                        } else if (etDetectMinFaceWidthSA!!.text.toString().isNullOrEmpty()) {
                            SnackBar.showValidationError(
                                context!!,
                                snackbarView!!,
                                "Please enter default minimum face width"
                            )
                            etDetectMinFaceWidthSA!!.requestFocus()
                        }else if(etRestartAppSA!!.text.toString().isNullOrEmpty()){
                            SnackBar.showValidationError(
                                context!!,
                                snackbarView!!,
                                "Please enter default restart time"
                            )
                            etRestartAppSA!!.requestFocus()
                        }else if(etRestartAppSA!!.text.toString().toInt()>25){
                            SnackBar.showValidationError(
                                context!!,
                                snackbarView!!,
                                "Enter restart time between 1 to 24"
                            )
                            etRestartAppSA!!.requestFocus()
                        } else {
                            BASE_Custom_URL = strSelectedServerOption!!
                            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                callCheckBaseURLAPI()
                            } else {
                                SnackBar.showInternetError(context!!, snackbarView!!)
                            }
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
                            try {
                                BASE_URL = strSelectedServerOption!!
                                val selectedOption: Int = rgGPSTrackSA!!.checkedRadioButtonId
                                var radioButton = findViewById(selectedOption) as RadioButton
                                var selectedGpsOption = radioButton.text
                                strSelectedGPSOption = selectedGpsOption.toString()
                                Log.e("selectedGpsOption = ", strSelectedGPSOption)
                                Log.e("selectedScreen = ", strSelectedScreenOption)
                                Log.e("selectedFacing = ", strSelectedCameraFacing)
                                Log.e("selectedServer = ", strSelectedServerOption)
                                Log.e("selectedMode = ", strSelectedAppModeOption)
                                //todo:: set data in session
                                SessionManager.setSelectedServerURL(
                                    context!!,
                                    strSelectedServerOption!!
                                )
                                SessionManager.setSelectedDefaultScreen(
                                    context!!,
                                    strSelectedScreenOption!!
                                )
                                SessionManager.setSelectedCameraFacing(
                                    context!!,
                                    strSelectedCameraFacing!!
                                )
                                SessionManager.setSelectedGPSOption(
                                    context!!,
                                    strSelectedGPSOption!!
                                )
                                SessionManager.setSelectedAppMode(
                                    context!!,
                                    strSelectedAppModeOption!!
                                )
                                SessionManager.setSelectedRestartAt(context!!,etRestartAppSA!!.text.toString())
                                Config.defaultMinFaceWidth =
                                    etDefaultMinWidthSA!!.getText().toString().toFloat()
                                Config.detectMinFaceWidth =
                                    etDetectMinFaceWidthSA!!.getText().toString().toFloat()
                                SessionManager.setDefaultFaceWidth(
                                    context!!,
                                    Config.defaultMinFaceWidth
                                )
                                SessionManager.setDetectFaceWidth(
                                    context!!,
                                    Config.detectMinFaceWidth
                                )
                                if (oldUrl == strSelectedServerOption) {
                                    //no need to login
                                    var builder = AlertDialog.Builder(context!!)
                                    builder.setCancelable(false)
                                    builder.setTitle("Settings")
                                    builder.setMessage("Setting saved successfully")
                                    builder.setPositiveButton(
                                        "Ok",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            dialog.dismiss()
                                            var intent =
                                                Intent(context, DashboardActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                            finish()
                                        })
                                    var dialog = builder.create()
                                    dialog.show()

                                } else {
                                    //need to login
                                    openLoginDialog()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            SnackBar.showError(
                                context!!,
                                snackbarView!!,
                                "Please enter valid URL"
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
                            "Please enter valid URL"
                        )
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            SnackBar.showError(
                context!!,
                snackbarView!!,
                "Please enter valid URL"
            )
        }

    }

    private fun openLoginDialog() {
        try {
            var dialog = Dialog(context!!)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_login)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            var etEmpIdDL = dialog.findViewById(R.id.etEmpIdDL) as EditText
            var etPasswordDL = dialog.findViewById(R.id.etPasswordDL) as EditText
            var rlLoginDL = dialog.findViewById(R.id.rlLoginDL) as RelativeLayout
            rlLoginDL.setOnClickListener {
                try {
                    var emailID = etEmpIdDL!!.text.toString()
                    var password = etPasswordDL!!.text.toString()
                    if (emailID.isNullOrEmpty()) {
                        Toast.makeText(context, "Please enter Email Id.", Toast.LENGTH_SHORT).show()
                        etEmpIdDL!!.requestFocus()
                    } else if (!GlobalMethods.isEmailValid(emailID!!)) {
                        Toast.makeText(context, "Please enter valid Email Id.", Toast.LENGTH_SHORT)
                            .show()
                        etEmpIdDL!!.requestFocus()
                    } else if (password.isNullOrEmpty()) {
                        Toast.makeText(context, "Please enter password.", Toast.LENGTH_SHORT).show()
                        etPasswordDL!!.requestFocus()
                    } else {
                        dialog.dismiss()
                        callNewLoginApi(emailID, password)
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
                                SessionManager.setKioskID(
                                    context!!,
                                    response.body().user.id.toString()
                                )
                                var intent = Intent(context, DashboardActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                            } else if (response.code() == 401) {
                                Toast.makeText(context!!, "User not found", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(
                                    context!!,
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT
                                ).show()
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

}