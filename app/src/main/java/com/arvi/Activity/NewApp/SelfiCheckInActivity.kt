package com.arvi.Activity.NewApp

import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.hardware.Camera
import android.location.Address
import android.location.Geocoder
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.Model.DetectFaceNewResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils.apiService
import com.arvi.SessionManager.SessionManager
import com.arvi.SessionManager.SessionManager.getFaceRecognizeOption
import com.arvi.SessionManager.SessionManager.getKioskID
import com.arvi.SessionManager.SessionManager.getKioskModel
import com.arvi.SessionManager.SessionManager.getOxiScanOption
import com.arvi.SessionManager.SessionManager.getSanitizerOption
import com.arvi.SessionManager.SessionManager.getToken
import com.arvi.Utils.*
import com.arvi.Utils.AppConstants.BEARER_TOKEN
import com.arvi.btScan.common.CameraSource
import com.arvi.btScan.common.CameraSourcePreview
import com.arvi.btScan.common.GraphicOverlay
import com.arvi.btScan.java.DafaultActivity
import com.arvi.btScan.java.arvi.ArviAudioPlaybacks
import com.arvi.btScan.java.arvi.ArviFaceDetectionProcessor
import com.arvi.btScan.java.arvi.Config
import com.arvi.btScan.java.arvi.FaceDetectionListener
import com.arvi.btScan.java.services.SlaveListener
import com.arvi.btScan.java.services.SlaveService
import com.arvi.btScan.java.services.SlaveService.MyServiceBinder
import com.google.gson.JsonObject
import com.societyguard.Utils.FileUtil.getImageUriAndPath
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SelfiCheckInActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
    SlaveListener,
    ConnectivityReceiver.ConnectivityReceiverListener {

    private var needToRestart: Boolean = false
    var tvInstruction: TextView? = null
    var imgVwHomeSCA: ImageView? = null
    var dialog: Dialog? = null
    private var TAG = "Face Capture"
    private val PERMISSION_REQUESTS = 1
    private var cameraSource: CameraSource? = null
    private var faceDetected = false
    private var faceOutside: kotlin.Boolean = false
    private var tempNormal: kotlin.Boolean = false
    private var facebitmap: Bitmap? = null
    private var isApiCalled = false
    private var strAddress = ""
    private var strCurrentDate: String? = null
    private var strCurrentTime: kotlin.String? = null

    private enum class STATE {
        UNKNOWN, INIT, WAIT_FOR_FACE, READ_TEMP, WAIT_FOR_TEMP, WAIT_OPERATE_LED, SHOW_RESULT_SCREEN, DELAY, WAIT_FOR_EXIT, WAIT_SANITIZER_ON, WAIT_SANITIZER_OFF
    }

    private var file1: MultipartBody.Part? = null
    private var slaveService: SlaveService? = null
    private var isServiceBound = false
    private var serviceConnection: ServiceConnection? = null
    private var serviceIntent: Intent? = null
    private var stateTimeoutIn100ms = 0
    private var faceLockTimeout: kotlin.Int = 0
    private var faceDetectTimeout: kotlin.Int = 0
    private var msgTimeout: kotlin.Int = 0
    private var tempRetry: kotlin.Int = 0
    private var resultToastTimeout: kotlin.Int = 0
    private var adjustAudioTimeout: kotlin.Int = 0
    private var threadRunning = false
    private var state = STATE.UNKNOWN
    private val previousState = STATE.UNKNOWN
    private val temperature = ""
    private var message: kotlin.String? = ""
    private val resultToast: Toast? = null

    var facingSwitch: ToggleButton? = null
    var facePreviewOverlay: GraphicOverlay? = null
    var faceCapturePreview: CameraSourcePreview? = null
    var fullname: String? = null
    var strUserId = ""
    var strUserName: kotlin.String? = ""
    var context: Context? = null
    var latitude = 0.0
    var longitude = 0.0

    var snackbarView: View? = null
    var h: Handler? = null
    var r: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfi_check_in)
        try {
            context = this@SelfiCheckInActivity
            ArviAudioPlaybacks.init(this.applicationContext)

            snackbarView = findViewById(android.R.id.content)
            facingSwitch = findViewById(R.id.facingSwitch)
            imgVwHomeSCA = findViewById(R.id.imgVwHomeSCA)
            faceCapturePreview = findViewById(R.id.faceCapturePreview)
            facePreviewOverlay = findViewById(R.id.facePreviewOverlay)
            facingSwitch!!.setOnCheckedChangeListener(this)
            tvInstruction = findViewById<View>(R.id.tvInstruction) as TextView

            /*for admin lite mode*/
            /*for all feature mode*/
            /*for visitor lite mode*/

            // Hide the toggle button if there is only 1 camera
            if (Camera.getNumberOfCameras() == 1) {
//                facingSwitch!!.setVisibility(View.GONE)
            }
            if (allPermissionsGranted()) {
                createCameraSource()
                startCameraSource()
                if (SessionManager.getSelectedCameraFacing(context!!) != null) {
                    if (SessionManager.getSelectedCameraFacing(context!!)
                            .equals(resources.getString(R.string.front_facing))
                    ) {
                        if (cameraSource != null) {
                            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                        }
                    } else {
                        if (cameraSource != null) {
                            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
                        }
                    }
                } else {
                    cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                }
            } else {
                getRuntimePermissions()
            }
            isServiceBound = false
            serviceIntent = Intent(applicationContext, SlaveService::class.java)
            if (intent.extras != null) {
                fullname = intent.getStringExtra("fullname")!!
                strUserId = intent.getStringExtra("userId")!!
            }
            var msg = "Please put your face inside border"
            if (fullname != null) {
                if (!fullname!!.isEmpty()) {
                    msg = "Hi $fullname!\n$msg"
                    tvInstruction!!.text = msg
                } else {
                    tvInstruction!!.text = msg
                }
            }

            imgVwHomeSCA!!.setOnClickListener {
                try {
                    if (cameraSource != null) {
                        cameraSource!!.stop()
                        cameraSource!!.release()
                        cameraSource = null
                    }
                    var intent = Intent(context, DashboardActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }



            SingleShotLocationProvider.requestSingleUpdate(
                applicationContext
            ) { location ->
                try {
                    Log.e(
                        "location:",
                        location.latitude.toString() + " , " + location.longitude
                    )
                    var latitude = location.latitude.toDouble()
                    var longitude = location.longitude.toDouble()
                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(applicationContext, Locale.getDefault())

                    addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses[0].getAddressLine(0)
                    Log.e("address:", address)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
//            setConnectionSpeedHandlerData()
            setHandlerData()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setHandlerData() {

        try {
            h = Handler(Looper.getMainLooper())
            r = object : Runnable {
                override fun run() {

                    //current time

                    val c: Calendar = Calendar.getInstance()
                    val hour: Int = c.get(Calendar.HOUR_OF_DAY)
                    val min: Int = c.get(Calendar.MINUTE)
                    val sec: Int = c.get(Calendar.SECOND)

                    var showHour = "11"
                    var showMinute = "59"
                    var showSec = "00"



                    if (hour < 10) {
                        if (hour == 0) {
                            showHour = "24"
                        } else {
                            showHour = "0" + hour.toString()
                        }
                    } else {
                        showHour = hour.toString()
                    }

                    if (min < 10) {
                        showMinute = "0" + min.toString()
                    } else {
                        showMinute = min.toString()
                    }

                    if (sec < 10) {
                        showSec = "0" + sec.toString()
                    } else {
                        showSec = sec.toString()
                    }


                    val currenttime =
                        "$showHour : $showMinute : $showSec"
                    Log.e("timeS:", currenttime)

                    var timeSelected = SessionManager.getSelectedRestartAt(context!!).toInt()
                    Log.e("timeSelected:",timeSelected.toString())
                    for (i in timeSelected..24 step timeSelected) {
                         var hour = i
                        var showHour = "00"
                        if(hour<10){
                            showHour = "0"+hour
                        }else{
                            showHour = hour.toString()
                        }
                        if(currenttime.equals(showHour+" : 00 : 00")){
                            try {
//                                Log.e("TimeD:",showHour+" : 00 : 00")
//                                Log.e("restart", "cleared cache")
                                if (facebitmap == null) {
                                    val intent = Intent(context!!, SplashActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    finish()
                                    startActivity(intent)
                                } else {
                                    needToRestart = true
                                }

                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                   /* if (currenttime.equals("03 : 01 : 00") || currenttime.equals("06 : 01 : 00") || currenttime.equals(
                            "09 : 01 : 00"
                        ) ||
                        currenttime.equals("10 : 45 : 00") || currenttime.equals("10 : 50  : 00") ||
                        currenttime.equals("12 : 01 : 00") || currenttime.equals("15 : 01 : 00") ||
                        currenttime.equals("18 : 01 : 00") || currenttime.equals("21 : 01 : 00") || currenttime.equals(
                            "23 : 59 : 59"
                        )
                    ) {
                        //todo:: clear app cache
                        try {
                            Log.e("restart", "cleared cache")
                            if (facebitmap == null) {
                                val intent = Intent(context!!, SplashActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                finish()
                                startActivity(intent)
                            } else {
                                needToRestart = true
                            }

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
*/

                    h!!.postDelayed(this, delayMillis)
                }
            }

            h!!.post(r as Runnable)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()!!) {
            if (!isPermissionGranted(this, permission!!)) {
                return false
            }
        }
        return true
    }

    private fun isPermissionGranted(
        context: Context,
        permission: String
    ): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    private fun getRequiredPermissions(): Array<String?>? {
        return try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.size > 0) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: java.lang.Exception) {
            showToastError(e)
            arrayOfNulls(0)
        }
    }

    private fun showToastError(e: java.lang.Exception) {
        //    Toast.makeText(context,""+e.printStackTrace(),Toast.LENGTH_SHORT).show()
    }

    private fun getRuntimePermissions() {
        try {
            val allNeededPermissions: MutableList<String?> =
                ArrayList()
            for (permission in getRequiredPermissions()!!) {
                if (!isPermissionGranted(this, permission!!)) {
                    allNeededPermissions.add(permission)
                }
            }
            if (!allNeededPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    allNeededPermissions.toTypedArray(),
                    PERMISSION_REQUESTS
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    private fun createCameraSource() {
        // If there's no existing cameraSource, create one.
        try {
            if (cameraSource == null) {
                cameraSource = CameraSource(this, facePreviewOverlay)
            }
            try {
                cameraSource!!.setMachineLearningFrameProcessor(
                    ArviFaceDetectionProcessor.getForDetectionScreen(
                        resources,
                        object : FaceDetectionListener {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            override fun faceDetected(face: Bitmap) {
                                try {
                                    facebitmap = face
                                    faceDetectTimeout = 100
                                    if (faceLockTimeout == 0) {
                                        faceDetected = true
                                    }
                                    if (facebitmap != null && !face.equals("")) {
                                        if (!isApiCalled) {
                                            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                                setConnectionSpeedHandlerData()
                                                callDetectFaceAPI(facebitmap!!)
                                            } else {
                                                SnackBar.showInternetError(
                                                    context!!,
                                                    snackbarView!!
                                                )
                                            }

                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    showToastError(e)
                                }
                            }

                            override fun faceErrorYangleFailed(
                                face: Bitmap,
                                yAngle: Float
                            ) {
                                try {
                                    facebitmap = face
                                    faceDetectTimeout = 100
                                    if (yAngle < 0) {
                                        if (state == STATE.WAIT_FOR_FACE) {
                                            showMessage("Turn your face towards left", 50, false)
                                        }
                                    } else {
                                        if (state == STATE.WAIT_FOR_FACE) {
                                            showMessage("Turn your face towards right", 50, false)
                                        }
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    showToastError(e)
                                }
                            }

                            override fun faceErrorZangleFailed(
                                face: Bitmap,
                                zAngle: Float
                            ) {
                                try {
                                    facebitmap = face
                                    faceDetectTimeout = 100
                                    if (state == STATE.WAIT_FOR_FACE) {
                                        showMessage("Keep your face straight", 50, false)
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    showToastError(e)
                                }
                            }

                            override fun faceErrorOutsideBox(face: Bitmap) {
                                try {
                                    facebitmap = face
                                    faceDetectTimeout = 100
                                    faceOutside = true
                                    if (state == STATE.WAIT_FOR_FACE) {
                                        showMessage("Adjust your face within the box", 50, false)
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    showToastError(e)
                                }
                            }

                            override fun faceErrorTooSmall(
                                face: Bitmap,
                                width: Float
                            ) {
                                try {
                                    facebitmap = face
                                    faceDetectTimeout = 100
                                    if (state == STATE.WAIT_FOR_FACE) {
                                        showMessage("Come forward", 50, false)
                                    }
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    showToastError(e)
                                }
                            }
                        })
                )
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Can not create image processor: ", e)
                Toast.makeText(
                    applicationContext,
                    "Can not create image processor: " + e.message,
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun callDetectFaceAPI(face: Bitmap) {
        try {
            isApiCalled = true

            val tempUri = getImageUriAndPath(this@SelfiCheckInActivity, face)
            val profilePath = tempUri
            Log.e("path ", profilePath!!)
            try {
                file1 = if (profilePath!!.isEmpty()) {
                    MultipartBody.Part.createFormData(
                        "file1", "",
                        RequestBody.create(MediaType.parse("multipart/form-data"), "")
                    )
                } else {
                    val file = File(profilePath)
                    MultipartBody.Part.createFormData(
                        "file1", file.name,
                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    )
                }
                var mAPIService: APIService? = null
                mAPIService = apiService
                val call = mAPIService.detectFace(
                    BEARER_TOKEN + getToken(
                        context!!
                    ), file1!!
                )
                call.enqueue(object : Callback<DetectFaceNewResponse> {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    override fun onResponse(
                        call: Call<DetectFaceNewResponse>,
                        response: Response<DetectFaceNewResponse>
                    ) {
                        try {
                            if (response.code() == 200) {
                                //      h!!.removeCallbacks(r!!)
                                Log.e("Upload", "success")
                                if (response.body().data == null) {
                                    strUserId = ""
                                    strUserName = "Unknown"
                                    fullname = strUserName
                                    showToast(
                                        tempNormal,
                                        temperature,
                                        message!!,
                                        strUserName
                                    )
                                } else {

                                    if (response.body().data != null) {
                                        if (response.body().data.employeeId != null) {
                                            strUserId = response.body().data.employeeId
                                        } else {
                                            strUserId = ""
                                        }


                                        if (response.body().data.name != null) {
                                            strUserName =
                                                response.body().data.name // response.body().getFullName();
                                        } else {
                                            strUserName = "Unknown"
                                        }

                                    }
                                    fullname = strUserName
                                    Log.e("userId:-", strUserId + "")
                                    if (strUserName != null && strUserName != "") {
                                        showToast(
                                            tempNormal,
                                            temperature,
                                            message!!,
                                            strUserName
                                        )
                                    } else {
                                        strUserId = ""
                                        strUserName = "Unknown"
                                        fullname = strUserName
                                        showToast(
                                            tempNormal,
                                            temperature,
                                            message!!,
                                            strUserName
                                        )
                                    }
                                }
                            } else if (response.code() == 401) {
                                //   h!!.removeCallbacks(r!!)
                                val intent = Intent(context, EnterLoginDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            showToastError(e)
                            //      h!!.removeCallbacks(r!!)
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    override fun onFailure(
                        call: Call<DetectFaceNewResponse>,
                        t: Throwable
                    ) {
                        try {
                            Log.e("Upload", "failure")
                            if (strUserName != null) showToast(
                                tempNormal,
                                temperature,
                                message!!,
                                strUserName
                            ) else {
                                strUserName = "Unknown"
                                strUserId = ""
                                fullname = strUserName
                                showToast(
                                    tempNormal,
                                    temperature,
                                    message!!,
                                    strUserName
                                )
                            }
                            Toast.makeText(
                                this@SelfiCheckInActivity,
                                "Not able to recognize face",
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
//                        h!!.removeCallbacks(r!!)
                        //       showToast(tempNormal, temperature, message, strUserName);
                    }
                })
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                //  h!!.removeCallbacks(r!!)
                // showToast(tempNormal, temperature, message, strUserName);
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
            //   h!!.removeCallbacks(r!!)
            // showToast(tempNormal, temperature, message, strUserName);
        }
    }

    private fun startCameraSource() {
        try {
            if (cameraSource != null) {
                try {


                    if (faceCapturePreview == null) {
                        Log.d(TAG, "resume: Preview is null")
                    }
                    if (facePreviewOverlay == null) {
                        Log.d(TAG, "resume: graphOverlay is null")
                    }
                    faceCapturePreview!!.start(cameraSource, facePreviewOverlay)

                    /*      if (cameraSource != null) {
                              if (cameraSource!!.getCameraFacing() == CameraSource.CAMERA_FACING_BACK) {
                                  cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                              } else {
                                  cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
                              }
                          }*/


                } catch (e: IOException) {
                    Log.e(TAG, "Unable to start camera source.", e)
                    cameraSource!!.release()
                    cameraSource = null
                    showToastError(e)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        try {
            Log.d(TAG, "Set facing")
            if (cameraSource != null) {
                if (isChecked) {
                    cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                } else {
                    cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
                }
            }
            faceCapturePreview!!.stop()
            startCameraSource()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun showMessage(
        msg: String,
        timeout: Int,
        priority: Boolean
    ) {
        var msg = msg
        try {
            if (msgTimeout == 0 || priority) {
                msgTimeout = timeout
                if (fullname != null) {
                    if (!fullname!!.isEmpty()) {
                        msg = "Hi $fullname!\n$msg"
                        tvInstruction!!.text = msg
                    } else {
                        tvInstruction!!.text = msg
                    }
                }
            } else {
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun showToast(
        result: Boolean,
        temp: String,
        msg: String,
        strUserName: String?
    ) {
        try {

            if (message != null && message != "") {
                if (message == "ENTRY DENIED") {
                    //     ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_high_denined);
                    if (getKioskModel(applicationContext) == "TX77" || getKioskModel(
                            applicationContext
                        ) == "TX99"
                    ) {
                        if (getSanitizerOption(
                                applicationContext
                            ) == "Enable"
                        ) {
                            ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_high)
                        } else {
                            ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_high_denined)
                        }
                    } else {
                        ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_high_denined)
                    }
                } else if (message == "NORMAL" || message!!.contains("NORMAL")) {
//                    ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_normal_pass);
                    if (getKioskModel(applicationContext) == "TX77" || getKioskModel(
                            applicationContext
                        ) == "TX99"
                    ) {
                        if (getSanitizerOption(
                                applicationContext
                            ) == "Enable"
                        ) {
                            ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_normal)
                        } else {
                            ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_normal_pass)
                        }
                    } else {
                        ArviAudioPlaybacks.forcePlay(R.raw.salli_temp_normal_pass)
                    }
                }
            }
            dialog = Dialog(this)
            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.dialog_result)
            val tvUserName =
                dialog!!.findViewById<View>(R.id.tvUserName) as TextView
            val tvEmpIdDR =
                dialog!!.findViewById<View>(R.id.tvEmpIdDR) as TextView
            val tvDateDR =
                dialog!!.findViewById<View>(R.id.tvDateDR) as TextView
            val tvTimeDR =
                dialog!!.findViewById<View>(R.id.tvTimeDR) as TextView
            val tvLocationDR =
                dialog!!.findViewById<View>(R.id.tvLocationDR) as TextView
            val imgVwStatusDR =
                dialog!!.findViewById<View>(R.id.imgVwStatusDR) as ImageView
            val tvMessageDR =
                dialog!!.findViewById<View>(R.id.tvMessageDR) as TextView
            val imgVwPhotoDR =
                dialog!!.findViewById<View>(R.id.imgVwPhotoDR) as ImageView
            if (facebitmap != null) {
                imgVwPhotoDR.setImageBitmap(facebitmap)
            }
            val df =
                DateTimeFormatter.ofPattern("dd MMM yyyy")
            val tf =
                DateTimeFormatter.ofPattern("HH:mm a")
            val now = LocalDateTime.now()
            println(df.format(now))
            tvDateDR.text = "Date: " + df.format(now)
            tvTimeDR.text = "Time: " + tf.format(now)
            strCurrentDate = df.format(now)
            strCurrentTime = tf.format(now)
            if (strUserName != null && strUserName != "") {
                tvUserName.text = strUserName
            }
            if (strUserId != null && strUserId != "") {
                imgVwStatusDR.setImageDrawable(resources.getDrawable(R.mipmap.ic_check_true))
                tvMessageDR.text = "Attendance capture successful"
            } else {
                tvUserName.text = "Unknown face"
                tvEmpIdDR.text = "Please contact administrator"
                tvDateDR.text = ""
                tvTimeDR.text = ""
                tvLocationDR.visibility = View.INVISIBLE
                imgVwStatusDR.setImageDrawable(resources.getDrawable(R.mipmap.ic_check_false))
                tvMessageDR.text = "Attendance capture failed"
            }
            SingleShotLocationProvider.requestSingleUpdate(
                applicationContext
            ) { location ->
                Log.e(
                    "location:",
                    location.latitude.toString() + " , " + location.longitude
                )
                latitude = location.latitude.toDouble()
                longitude = location.longitude.toDouble()
                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(applicationContext, Locale.getDefault())
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses[0].getAddressLine(0)
                    val city = addresses[0].locality
                    val state = addresses[0].adminArea
                    val country = addresses[0].countryName
                    val postalCode = addresses[0].postalCode
                    val knownName = addresses[0].featureName
                    strAddress = address
                    /*+", "+city+", "+state+", "+country+", "+postalCode*/Log.e(
                        "address:",
                        address
                    )
                    if (SessionManager.getSelectedGPSOption(context!!) != null && SessionManager.getSelectedGPSOption(
                            context!!
                        ).equals("Yes")
                    ) {
                        tvLocationDR.text = "Location: $address"
                    } else {
                        tvLocationDR.text = ""
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    showToastError(e)
                }
            }
            if (fullname != null) {
                if (fullname != "") {
                    tvUserName.visibility = View.VISIBLE
                    tvUserName.text = "Hi, $fullname"
                } else {
                    tvUserName.visibility = View.GONE
                }
            } else {
                tvUserName.visibility = View.GONE
            }
            if (dialog!!.isShowing) {
            } else {
                dialog!!.show()
            }
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            resultToastTimeout = if (getKioskModel(applicationContext) == "TX55" || getKioskModel(
                    applicationContext
                ) == "TX66"
            ) {
                1000
            } else {
                if (getSanitizerOption(applicationContext) == "Enabled") {
                    1000
                } else {
                    1000
                }
            }

            if (needToRestart) {
                needToRestart = false
                val intent = Intent(context!!, SplashActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                finish()
                startActivity(intent)
            } else {
                goBackScreen()
            }
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    override fun processResponseFromSlave(data: ByteArray?) {
        Log.d(TAG, "processResponseFromSlave")
    }

    val delayMillis: Long = 1000
//    var h: Handler? = null
//    var r: Runnable? = null

    private fun setConnectionSpeedHandlerData() {
/*
        try {
            h = Handler(Looper.getMainLooper())
            r = object : Runnable {

                override fun run() {
                    Log.e("downSpeed", TrafficUtils.getNetworkSpeed())
                    if(TrafficUtils.getNetworkSpeed().contains("KB")){
                        var speed = TrafficUtils.getNetworkSpeed()
                        speed = speed.substring(0,speed.indexOf(" "))
                        if(speed.toInt()<20){
//                            Toast.makeText(context,"Your internet speed is slow",Toast.LENGTH_SHORT).show()
                            SnackBar.showNetSpeedError(context!!,snackbarView!!,"Your internet speed is slow")
                        }
                    }
                    h!!.postDelayed(this, delayMillis)
                }
            }

            h!!.post(r as Runnable)


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
*/
    }


    override fun every100ms() {
        try {
            if (stateTimeoutIn100ms > 0) {
                stateTimeoutIn100ms--
            }
            if (faceLockTimeout > 0) {
                faceLockTimeout--
            }
            if (faceDetectTimeout > 0) {
                faceDetectTimeout--
            }
            if (msgTimeout > 0) {
                msgTimeout--
            }
            if (resultToastTimeout > 0) {
                resultToastTimeout--
            }
            if (adjustAudioTimeout > 0) {
                adjustAudioTimeout--
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    //address, empid, date and time
    private fun callStoreTempApi() {
        try {
            val strTemp = temperature.replace(" F", "")
            val jsonObject = JsonObject()
            val empId = getKioskID(context!!)
            jsonObject.addProperty("employeeId", strUserId)
            jsonObject.addProperty("scanDate", strCurrentDate)
            jsonObject.addProperty("scanTime", strCurrentTime)

            if (SessionManager.getSelectedGPSOption(context!!) != null && SessionManager.getSelectedGPSOption(
                    context!!
                ).equals("Yes")
            ) {
                jsonObject.addProperty("address", strAddress)
                jsonObject.addProperty("emp_lat", latitude)
                jsonObject.addProperty("emp_long", longitude)
            }

            Log.e("storeT:", jsonObject.toString())
            var mAPIService: APIService? = null
            mAPIService = apiService
            val call = mAPIService.recordUserTemperature(
                BEARER_TOKEN + getToken(context!!),
                "application/json",
                jsonObject
            )
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    Log.e("Store Temp", "success")
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    Log.e("Store temp", "failure")
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    private fun goBackScreen() {
        try {
            cameraSource!!.stop()
            Log.d(TAG, "GO Back ! ")
            DafaultActivity.setNextFaceTimeout(3)

            //Todo:: priyanka go for oximeter reading screen
            if (getKioskModel(applicationContext) == "TX99" && getOxiScanOption(
                    applicationContext
                ) == "Enable"
            ) {
                try {
                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            dialog!!.dismiss()
                            /*   val i =
                                   Intent(applicationContext, DashboardActivity::class.java)
                               startActivity(i)*/
                            openDashboard()
                        }
                    }, 5000)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    showToastError(e)
                }
            } else {
                try {
                    val timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            dialog!!.dismiss()

                            //todo::Priyanka 06-07 start
                            if (getFaceRecognizeOption(
                                    applicationContext
                                ) == "ON"
                            ) {
                                if (strUserId != null) {
                                    if (strUserId != "") {
                                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                            callStoreTempApi()
                                        } else {
                                            SnackBar.showInternetError(context!!, snackbarView!!)
                                        }

                                    } else {
                                        strUserId = ""

                                        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                            callStoreTempApi()
                                        } else {
                                            SnackBar.showInternetError(context!!, snackbarView!!)
                                        }

                                    }
                                } else {
                                    strUserId = ""

                                    if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                        callStoreTempApi()
                                    } else {
                                        SnackBar.showInternetError(context!!, snackbarView!!)
                                    }

                                }
                            }
                            //todo:: 06-07 end
                            if (dialog != null) {
                                if (dialog!!.isShowing) {
                                    dialog!!.dismiss()
                                }
                            }
                            openDashboard()
                            /*   val i =
                                   Intent(applicationContext, DashboardActivity::class.java)
                               startActivity(i)
                               finish()*/
                        }
                    }, 5000)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    showToastError(e)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    private fun openDashboard() {
        try {
            /*  val i =
                                Intent(applicationContext, DashboardActivity::class.java)
                            startActivity(i)*/
            try {
                isApiCalled = false
                SlaveService.serviceOn = true
                resultToastTimeout = 0
                stateTimeoutIn100ms = 100
                faceDetectTimeout = Config.detectTimeoutSec * 10
                state = STATE.INIT

                if (!threadRunning) {
                    threadRunning = true
                    state = STATE.UNKNOWN
                    val t = Thread(Runnable {
                        Log.d(TAG, "activity thread started")
                        while (threadRunning) {
                            try {
                                if (isServiceBound) {
                                    stateMachine()
                                }
                                Thread.sleep(10)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                showToastError(e)
                            }
                        }
                        Log.d(TAG, "thread outside while")
                    })
                    TAG += "(" + t.id + ")"
                    t.start()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                showToastError(e)
            }
            //    startService(serviceIntent)
            //bindService()
            faceDetected = false
            if (allPermissionsGranted()) {
                createCameraSource()
                startCameraSource()
            } else {
                getRuntimePermissions()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    private fun bindService() {
        try {
            Log.d(TAG, "bindService")
            if (serviceConnection == null) {
                serviceConnection = object : ServiceConnection {
                    override fun onServiceConnected(
                        componentName: ComponentName,
                        iBinder: IBinder
                    ) {
                        try {
                            val myServiceBinder = iBinder as MyServiceBinder
                            slaveService = myServiceBinder.service
                            isServiceBound = true
                            Log.d(
                                TAG,
                                "onServiceConnected. setting owner listener " + slaveService
                            )
                            slaveService!!.addListener(this@SelfiCheckInActivity)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            showToastError(e)
                        }
                    }

                    override fun onServiceDisconnected(componentName: ComponentName) {
                        isServiceBound = false
                    }
                }
            }
            //   bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
        try {
            SlaveService.serviceOn = true
            resultToastTimeout = 0
            stateTimeoutIn100ms = 100
            faceDetectTimeout = Config.detectTimeoutSec * 10
            state = STATE.INIT

            if (!threadRunning) {
                threadRunning = true
                state = STATE.UNKNOWN
                val t = Thread(Runnable {
                    Log.d(TAG, "activity thread started")
                    while (threadRunning) {
                        try {
                            if (isServiceBound) {
                                stateMachine()
                            }
                            Thread.sleep(10)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            showToastError(e)
                        }
                    }
                    Log.d(TAG, "thread outside while")
                })
                TAG += "(" + t.id + ")"
                t.start()
            }
            if (allPermissionsGranted()) {
                createCameraSource()
                startCameraSource()
            } else {
                getRuntimePermissions()
            }
            faceCapturePreview!!.stop()
            startCameraSource()
            if (SessionManager.getSelectedCameraFacing(context!!) != null) {
                if (SessionManager.getSelectedCameraFacing(context!!)
                        .equals(resources.getString(R.string.front_facing))
                ) {
                    if (cameraSource != null) {
                        cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                    }
                } else {
                    if (cameraSource != null) {
                        cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
                    }
                }
            } else {
                cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
            }


            try {
                val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
                connectivityChangeReceiver = ConnectivityReceiver()

                registerReceiver(connectivityChangeReceiver, filter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        try {
            MyApplication.instance!!.setConnectivityListener(this)
            // startService(serviceIntent)
//            bindService()
            faceDetected = false

            if (cameraSource == null) {
                SlaveService.serviceOn = true
                resultToastTimeout = 0
                stateTimeoutIn100ms = 100
                faceDetectTimeout = Config.detectTimeoutSec * 10
                state = STATE.INIT

                if (!threadRunning) {
                    threadRunning = true
                    state = STATE.UNKNOWN
                    val t = Thread(Runnable {
                        Log.d(TAG, "activity thread started")
                        while (threadRunning) {
                            try {
                                if (isServiceBound) {
                                    stateMachine()
                                }
                                Thread.sleep(10)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                showToastError(e)
                            }
                        }
                        Log.d(TAG, "thread outside while")
                    })
                    TAG += "(" + t.id + ")"
                    t.start()
                }
                if (allPermissionsGranted()) {
                    createCameraSource()
                    startCameraSource()
                } else {
                    getRuntimePermissions()
                }
                faceCapturePreview!!.stop()
                startCameraSource()
                if (SessionManager.getSelectedCameraFacing(context!!) != null) {
                    if (SessionManager.getSelectedCameraFacing(context!!)
                            .equals(resources.getString(R.string.front_facing))
                    ) {
                        if (cameraSource != null) {
                            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                        }
                    } else {
                        if (cameraSource != null) {
                            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_BACK)
                        }
                    }

                } else {
                    cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
                }
            }


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
        try {
            if (slaveService != null) {
                slaveService!!.removeListener()
            }
            if (isServiceBound) {
                unbindService(serviceConnection!!)
                isServiceBound = false
            }
            if (cameraSource != null) {
                cameraSource!!.stop()
                cameraSource!!.release()
                cameraSource = null
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
        try {
            threadRunning = false
            unregisterReceiver(connectivityChangeReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            showToastError(e)
        }

    }


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        NoInternetDialog.showSnack(isConnected, context!!)
    }


    override fun onBackPressed() {
        /* try {

             var builder = AlertDialog.Builder(context!!)
             builder.setCancelable(false)
             builder.setTitle("Arvi")
             builder.setMessage("You want to open Home page?")
             builder.setPositiveButton(
                 "Yes",
                 DialogInterface.OnClickListener { dialog, which ->
                     dialog.dismiss()
                     var intent = Intent(context, DashboardActivity::class.java)
                     intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                     startActivity(intent)
                 })
             builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                 dialog.dismiss()
                 finish()
             })
             var dialog = builder.create()
             dialog.show()

           //  openSettingScreen(this@SelfiCheckInActivity)
         } catch (e: java.lang.Exception) {
             e.printStackTrace()
         }*/
        finish()
    }

    public fun stateMachine() {

    }

    var connectivityChangeReceiver: ConnectivityReceiver? = null
}