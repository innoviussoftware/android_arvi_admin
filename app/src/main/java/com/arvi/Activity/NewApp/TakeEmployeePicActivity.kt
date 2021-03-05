package com.arvi.Activity.NewApp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.Model.GetAddEmployeeResponse
import com.arvi.Model.UploadPhotoData
import com.arvi.Model.UploadPhotoResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils.apiService
import com.arvi.SessionManager.SessionManager.getToken
import com.arvi.Utils.AppConstants.BEARER_TOKEN
import com.arvi.Utils.MyProgressDialog.hideProgressDialog
import com.arvi.btScan.common.CameraSource
import com.arvi.btScan.common.CameraSourcePreview
import com.arvi.btScan.common.GraphicOverlay
import com.arvi.btScan.java.arvi.ArviAudioPlaybacks
import com.arvi.btScan.java.arvi.ArviFaceDetectionProcessor
import com.arvi.btScan.java.arvi.FaceDetectionListener
import com.arvi.btScan.java.services.SlaveListener
import com.arvi.btScan.java.services.SlaveService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.societyguard.Utils.FileUtil
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*

class TakeEmployeePicActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
    SlaveListener, View.OnClickListener {


    internal var tvInstruction: TextView? = null
    private val TAG = "Add Visitor Photo"
    private val PERMISSION_REQUESTS = 1
    private var cameraSource: CameraSource? = null
    private var faceDetected = false
    private var faceOutside = false
    private var isDoneCapture: String? = null
    private var photoCount = 0
    private var file1: MultipartBody.Part? = null
    private var token: String? = null
    private var newFace: Bitmap? = null
    private var strEmpId: String? = null
    private var strPhone: String? = null


    private enum class STATE {
        UNKNOWN, INIT, WAIT_FOR_FACE, READ_TEMP, WAIT_FOR_TEMP, WAIT_FOR_EXIT
    }

    private var slaveService: SlaveService? = null
    private var isServiceBound: Boolean = false
    private var serviceConnection: ServiceConnection? = null
    private var serviceIntent: Intent? = null
    private var threadRunning = false
    private var state = STATE.UNKNOWN
    private var previousState = STATE.UNKNOWN


    internal var facingSwitch: ToggleButton? = null
    internal var facePreviewOverlay: GraphicOverlay? = null
    internal var faceCapturePreview: CameraSourcePreview? = null
    internal var img1: ImageView? = null
    internal var img2: ImageView? = null
    internal var img3: ImageView? = null
    internal var img4: ImageView? = null
    internal var img5: ImageView? = null
    internal var isImg1Seted: Boolean? = false
    internal var isImg2Seted: Boolean? = false
    internal var isImg3Seted: Boolean? = false
    internal var isImg4Seted: Boolean? = false
    internal var isImg5Seted: Boolean? = false
    internal var imgCount = 0
    internal var name: String? = ""
    internal var context: Context? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_employee_pic)
        try {
            context = this@TakeEmployeePicActivity
            ArviAudioPlaybacks.init(this.applicationContext)
            facingSwitch = findViewById(R.id.facingSwitch)
            faceCapturePreview = findViewById(R.id.faceCapturePreview)
            facePreviewOverlay = findViewById(R.id.facePreviewOverlay)
            facingSwitch!!.setOnCheckedChangeListener(this)
            tvInstruction = findViewById<View>(R.id.tvInstruction) as TextView
            tvInstruction!!.setOnClickListener(this)
            img1 = findViewById(R.id.img1)
            img2 = findViewById(R.id.img2)
            img3 = findViewById(R.id.img3)
            img4 = findViewById(R.id.img4)
            img5 = findViewById(R.id.img5)

            if (Camera.getNumberOfCameras() == 1) {
                //facingSwitch.setVisibility(View.GONE);
            }
            if (allPermissionsGranted()) {
                createCameraSource()
                startCameraSource()
            } else {
                getRuntimePermissions()
            }

            isServiceBound = false
            serviceIntent = Intent(applicationContext, SlaveService::class.java)

            if (intent.extras != null) {
                name = intent.getStringExtra("name")
                token = getToken(context!!)
                // getIntent().getStringExtra("token");
                strPhone = intent.getStringExtra("mobile")
                strEmpId = intent.getStringExtra("employeeId")
//                String msg = "Please put your face inside border";
                //todo:: priyanka 27-10
                /*if (name != null) {
                    if (!name.isEmpty()) {
                        msg = "Hi " + name + "!\n" + msg;
                        tvInstruction.setText(msg);
                    } else {
                        tvInstruction.setText(msg);
                    }
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        } catch (e: Exception) {
            e.printStackTrace()
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
                            val myServiceBinder = iBinder as SlaveService.MyServiceBinder
                            slaveService = myServiceBinder.service
                            isServiceBound = true
                            Log.d(TAG, "onServiceConnected. setting owner listener $slaveService")
                            slaveService!!.addListener(this@TakeEmployeePicActivity)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onServiceDisconnected(componentName: ComponentName) {
                        isServiceBound = false
                    }
                }
            }
            bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvInstruction ->

                if (!isImg1Seted!!) {
                    try {

                        img1!!.setImageBitmap(newFace)
                        isDoneCapture = "front"
                        if (newFace == null) {
                            Toast.makeText(context!!, "Face not detected", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            isImg1Seted = true
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (!isImg2Seted!!) {
                    try {

                        img2!!.setImageBitmap(newFace)
                        isDoneCapture = "front,right"

                        if (newFace == null) {
                            Toast.makeText(context!!, "Face not detected", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            isImg2Seted = true
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (!isImg3Seted!!) {
                    try {

                        img3!!.setImageBitmap(newFace)
                        isDoneCapture = "front,left"

                        if (newFace == null) {
                            Toast.makeText(context!!, "Face not detected", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            isImg3Seted = true
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else if (!isImg4Seted!!) {
                    try {

                        img4!!.setImageBitmap(newFace)

                        if (newFace == null) {
                            Toast.makeText(context!!, "Face not detected", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            isImg4Seted = true
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null)
        return Uri.parse(path)
    }

    private fun openNextScreen(face: Bitmap) {
        try {

            imgCount = imgCount + 1
            val tempUri = getImageUri(context!!, face)
            val profilePath = FileUtil.getPath(context!!, tempUri)
            Log.e("path ", profilePath!!)
            callStorePersonPicApi(profilePath)
            if (imgCount == 4) {
                var message= "Welcome "+ name+" , Your onboarding is complete"
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                builder.setMessage(message)
                builder.setPositiveButton("Ok") { dialog, which ->
                    try {
                        dialog.dismiss()
                        val intent =
                            Intent(applicationContext, DashboardActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val dialog = builder.create()
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun callStorePersonPicApi(profilePath: String) {
        try {
            file1 = if (profilePath.isEmpty()) {
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
            val call = mAPIService.uploadUserPhoto(BEARER_TOKEN + token, file1!!)
            call.enqueue(object : Callback<UploadPhotoResponse> {
                override fun onResponse(
                    call: Call<UploadPhotoResponse>,
                    response: Response<UploadPhotoResponse>
                ) {
                    Log.e("Upload", "success")
                    try {
                        val alPhotoDetail = ArrayList<UploadPhotoData>()
                        if (response.body().data != null)
                            alPhotoDetail.addAll(response.body().data!!)
                        callStoreWithId(alPhotoDetail)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<UploadPhotoResponse>, t: Throwable) {
                    Log.e("Upload", "failure")
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun callStoreWithId(response: ArrayList<UploadPhotoData>) {
        try {
            if (response.size > 0) {
                val jsonObject = JsonObject()
                val jsonArray = JsonArray()
                val jsonImgObject = JsonObject()
                val path = response[0].path
                val mimetype = response[0].mimetype
                val filename = response[0].filename
                jsonImgObject.addProperty("path", path)
                jsonImgObject.addProperty("mimetype", mimetype)
                jsonImgObject.addProperty("filename", filename)
                jsonObject.addProperty("mobile", strPhone)
                jsonObject.addProperty("email", "")
                jsonObject.addProperty("employeeId", strEmpId)
                jsonObject.addProperty("name", name)

//            jsonArray.add("images",jsonImgObject);
                jsonArray.add(jsonImgObject)
                jsonObject.add("images", jsonArray)
                var mAPIService: APIService? = null
                mAPIService = apiService
                val context: Context = this@TakeEmployeePicActivity
                val call = mAPIService.addEmployee(
                    "application/json",
                    "Bearer " + getToken(context),
                    jsonObject
                )
                call.enqueue(object : Callback<GetAddEmployeeResponse?> {
                    override fun onResponse(
                        call: Call<GetAddEmployeeResponse?>,
                        response: Response<GetAddEmployeeResponse?>
                    ) {
                        Log.e("Upload", "success")
                    }

                    override fun onFailure(call: Call<GetAddEmployeeResponse?>, t: Throwable) {
                        Log.e("Upload", "failure")
                    }
                })

            }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                hideProgressDialog()

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


                } catch (e: IOException) {
                    Log.e(TAG, "Unable to start camera source.", e)
                    cameraSource!!.release()
                    cameraSource = null
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission!!)) {
                return false
            }
        }
        return true
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    private fun getRequiredPermissions(): Array<String?> {
        try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            return if (ps != null && ps.size > 0) {
                ps
            } else {
                arrayOfNulls(0)
            }
        } catch (e: Exception) {
            return arrayOfNulls(0)
        }

    }

    private fun getRuntimePermissions() {
        try {
            val allNeededPermissions = ArrayList<String>()
            for (permission in getRequiredPermissions()) {
                if (!isPermissionGranted(this, permission!!)) {
                    allNeededPermissions.add(permission)
                }
            }

            if (!allNeededPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toTypedArray(), PERMISSION_REQUESTS
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createCameraSource() {
        try {
            if (cameraSource == null) {
                cameraSource = CameraSource(this, facePreviewOverlay)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            //todo:: priyanka 27-10

            cameraSource!!.setMachineLearningFrameProcessor(
                ArviFaceDetectionProcessor.getForDetectionScreen(
                    resources, object :
                        FaceDetectionListener {
                        override fun faceDetected(face: Bitmap) {

                            try {
                                newFace = face

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun faceErrorYangleFailed(face: Bitmap, yAngle: Float) {
                            try {
                                Log.e("Angle Y: ", yAngle.toString())
                                newFace = face

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun faceErrorZangleFailed(face: Bitmap, zAngle: Float) {
                            try {
                                newFace = face
                                Log.e("Angle z:", zAngle.toString())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun faceErrorOutsideBox(face: Bitmap) {
                            try {
                                faceOutside = true
                                if (state == STATE.WAIT_FOR_FACE) {
                                    Toast.makeText(
                                        context,
                                        "Adjust your face within the box",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                        override fun faceErrorTooSmall(face: Bitmap, width: Float) {
                            try {
                                faceOutside = true
                                if (state == STATE.WAIT_FOR_FACE) {
                                    Toast.makeText(
                                        context,
                                        "Come forward",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // showMessage("Come forward", 50, false);
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    })
            )
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: ", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            if (requestCode == PERMISSION_REQUESTS) {
                if (allPermissionsGranted()) {
                    createCameraSource()
                    startCameraSource()
                } else {
                    getRuntimePermissions()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onStart() {
        try {
            Log.d(TAG, "onStart")
            super.onStart()
            SlaveService.serviceOn = true
            this.state = STATE.INIT
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!threadRunning) {
            threadRunning = true

            state = STATE.UNKNOWN

            Thread(Runnable {
                Log.d(TAG, "activity thread started")
                while (threadRunning) {
                    try {
                        if (isServiceBound) {
                            stateMachine()
                        }
                        Thread.sleep(10)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                Log.d(TAG, "thread outside while")
            }).start()
        }
    }

    fun stateMachine() {

        try {
            if (state != previousState) {
                Log.d(TAG, "$previousState--->$state")
                previousState = state
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        when (state) {
            STATE.INIT -> try {
                if (SlaveService.connected) {
                    if (SlaveService.triggerTimeout == 0) {
                    } else {
                    }
                    state = STATE.WAIT_FOR_FACE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            STATE.WAIT_FOR_FACE -> {
            }
            STATE.READ_TEMP -> {
            }
            STATE.WAIT_FOR_TEMP -> {
            }
            STATE.WAIT_FOR_EXIT -> {
            }
            else -> {
                state = STATE.INIT
            }
        }
    }


    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        try {
            startService(serviceIntent)
            bindService()
            faceDetected = false
        } catch (e: Exception) {
            e.printStackTrace()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
        try {
            threadRunning = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun processResponseFromSlave(data: ByteArray?) {
    }

    override fun every100ms() {
    }


}