package com.arvi.Activity.NewApp

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.Model.NewVisitorRegisterResponse
import com.arvi.Model.UploadPhotoData
import com.arvi.Model.UploadPhotoResponse
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.*
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
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddVisitorPhotoActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener,
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

    //private var token: String? = null
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
    internal var visitorName: String? = ""
    internal var context: Context? = null
    var visitorEntryId: Int = 0
    var comingFrom:String = ""
    var visitingToId:Int=0


    var snackbarView: View? = null

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
                            slaveService!!.addListener(this@AddVisitorPhotoActivity)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onServiceDisconnected(componentName: ComponentName) {
                        isServiceBound = false
                    }
                }
            }
            //bindService(serviceIntent, serviceConnection!!, Context.BIND_AUTO_CREATE)
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
            //   startService(serviceIntent)
            //bindService()
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
            if(cameraSource!=null) {
                cameraSource!!.stop()
                cameraSource!!.release()
                cameraSource = null
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


    lateinit var visitingToName: String
    lateinit var expectDate: String
    lateinit var expectTime: String
    lateinit var company: String
    lateinit var purpose: String
    lateinit var visitorMobile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_visitor_photo)
        try {
            context = this@AddVisitorPhotoActivity
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
            snackbarView = findViewById(android.R.id.content)
            newFace = null
            if (Camera.getNumberOfCameras() == 1) {
                //facingSwitch.setVisibility(View.GONE);
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            jsonUserPicObject = JsonObject()
            jsonArray = JsonArray()
            visitorName = intent.getStringExtra("visitorName")
            visitingToName = intent.getStringExtra("visitingToName")!!
            expectDate = intent.getStringExtra("expectDate")!!
            expectTime = intent.getStringExtra("expectTime")!!
            visitorMobile = intent.getStringExtra("visitorMobile")!!
            visitorEntryId = intent.getIntExtra("visitorEntryId", 0)
            visitingToId = intent.getIntExtra("visitingToId",0)
            comingFrom = intent.getStringExtra("comingFrom")!!
            purpose = intent.getStringExtra("purpose")!!



        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onClick(view: View) {
        try {
            when (view.id) {
                R.id.tvInstruction ->

                    if (!isImg1Seted!!) {
                        if (newFace != null) {
                            isImg1Seted = true
                            img1!!.setImageBitmap(newFace)
                            isDoneCapture = "front"
                            photoCount = photoCount + 1
                            /*Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()*/
                            openNextScreen(newFace!!)
                        }
                    } else if (!isImg2Seted!!) {
                        if (newFace != null) {
                            isImg2Seted = true
                            img2!!.setImageBitmap(newFace)
                            isDoneCapture = "front,right"
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    } else if (!isImg3Seted!!) {
                        if (newFace != null) {
                            isImg3Seted = true
                            img3!!.setImageBitmap(newFace)
                            isDoneCapture = "front,left"
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    } else if (!isImg4Seted!!) {
                        if (newFace != null) {
                            isImg4Seted = true
                            img4!!.setImageBitmap(newFace)
                            photoCount = photoCount + 1
                            Toast.makeText(
                                applicationContext,
                                "Photo $photoCount is captured",
                                Toast.LENGTH_SHORT
                            ).show()
                            openNextScreen(newFace!!)
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "IMG_" + Calendar.getInstance().getTime(),
            null
        )
        return Uri.parse(path)
    }

    private fun openNextScreen(face: Bitmap) {
        try {

            imgCount = imgCount + 1
/*            val tempUri = getImageUri(context!!, face)
            val profilePath = FileUtil.getPath(context!!, tempUri)*/

            val tempUri = FileUtil.getImageUriAndPath(this@AddVisitorPhotoActivity, face)
            val profilePath = tempUri
            Log.e("path ", profilePath!!)

            if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                callStorePersonPicApi(profilePath)
            } else {
                SnackBar.showInternetError(context!!, snackbarView!!)
            }



            /*if (imgCount == 1) {
                cameraSource!!.stop()
                cameraSource!!.release()
                cameraSource = null

                val builder = AlertDialog.Builder(this)
                builder.setCancelable(false)
                var message = "Welcome " + visitorName + ", You have been registered"
                builder.setMessage(message)
                builder.setPositiveButton("Ok") { dialog, which ->
                    try {
                        dialog.dismiss()


                        *//* val intent =
                             Intent(applicationContext, DashboardActivity::class.java)
                         intent.flags =
                             Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                         startActivity(intent)*//*
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val dialog = builder.create()
                dialog.show()
            }*/
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
            mAPIService = ApiUtils.apiService
            val call = mAPIService.uploadUserPhoto(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(this),
                file1!!
            )
            MyProgressDialog.showProgressDialog(context!!)
            call.enqueue(object : Callback<UploadPhotoResponse> {
                override fun onResponse(
                    call: Call<UploadPhotoResponse>,
                    response: Response<UploadPhotoResponse>
                ) {
                    Log.e("Upload", "success")
                    try {
                        val alPhotoDetail = ArrayList<UploadPhotoData>()
                        if (response.code() == 200) {
                            if (response.body().data != null)
                                alPhotoDetail.addAll(response.body().data!!)
                            callStoreWithId(alPhotoDetail)
                        } else if (response.code() == 401) {
                            var intent = Intent(context!!, EnterCompanyDetailActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            SessionManager.clearAppSession(context!!)
                        }
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

    lateinit var jsonUserPicObject: JsonObject
    lateinit var jsonArray: JsonArray
    private fun callStoreWithId(response: ArrayList<UploadPhotoData>) {
        try {
            if (response.size > 0) {

                val jsonObject = JsonObject()

                val jsonImgObject = JsonObject()
                val path = response[0].path
                val mimetype = response[0].mimetype
                val filename = response[0].filename
                jsonImgObject.addProperty("path", path)
                jsonImgObject.addProperty("mimetype", mimetype)
                jsonImgObject.addProperty("filename", filename)
                jsonArray.add(jsonImgObject)

                jsonObject.addProperty("mobile", strPhone)
                jsonObject.addProperty("email", "")
                jsonObject.addProperty("employeeId", strEmpId)
                jsonObject.addProperty("name", visitorName)
//                jsonArray.add("images", jsonImgObject);


                jsonUserPicObject.addProperty("name", visitorName!!)
                jsonUserPicObject.addProperty("mobile", visitorMobile!!)
                jsonUserPicObject.add("images", jsonArray)
                jsonUserPicObject.addProperty("type", purpose)

                if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                    callAddNewEntriesApi()
                } else {
                    SnackBar.showInternetError(context!!, snackbarView!!)
                }

/*
//new
                var visitorJsonObject = JsonObject()
                visitorJsonObject.addProperty("name",visitorName)
                visitorJsonObject.addProperty("mobile",visitorMobile)
                jsonUserPicObject.add("visitor",visitorJsonObject)

                var visitingToJsonObject = JsonObject()
                visitingToJsonObject.addProperty("id",visitingToId)
                jsonUserPicObject.add("visitingTo",visitingToJsonObject)

                var c = Calendar.getInstance().time
                System.out.println("Current time => " + c)
                var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'", Locale.getDefault())

                val timeZone: TimeZone = TimeZone.getTimeZone("IST")
                df.timeZone  = timeZone
                var formattedActualDate: String = df.format(c)
                formattedActualDate = GlobalMethods.convertISTToUTCDate(formattedActualDate)

                var dataJsonObject = JsonObject()
                dataJsonObject.addProperty("actualEntryTime",formattedActualDate)
                dataJsonObject.addProperty("purpose",purpose)
                dataJsonObject.addProperty("comingFrom",comingFrom)
                dataJsonObject.add("images",jsonArray)
                jsonUserPicObject.add("data",dataJsonObject)
*/



            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }

    }


    private fun callAddNewEntriesApi() {
        try {
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
//            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.newVisitorsEntryRegister(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                "application/json",
                jsonUserPicObject
            )
                .enqueue(object : Callback<NewVisitorRegisterResponse> {

                    override fun onResponse(
                        call: Call<NewVisitorRegisterResponse>,
                        response: Response<NewVisitorRegisterResponse>
                    ) {
//                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                var visitorID = response.body().visitor.id
                                if (ConnectivityDetector.isConnectingToInternet(context!!)) {
                                    callSameVisitorAddDetailsApi(visitorID)
                                } else {
                                    MyProgressDialog.hideProgressDialog()
                                    SnackBar.showInternetError(context!!, snackbarView!!)
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
                        call: Call<NewVisitorRegisterResponse>,
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


    private fun callSameVisitorAddDetailsApi(visitorID: Int) {
        try {
/*
            var c = Calendar.getInstance().time
            System.out.println("Current time => " + c);
*/

            //     var df = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())

/*
            var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'", Locale.getDefault())

            val timeZone: TimeZone = TimeZone.getTimeZone("IST")
            df.timeZone  = timeZone
            var formattedDate: String = df.format(c)
            formattedDate = GlobalMethods.convertISTToUTCDate(formattedDate)
*/

/*
            var jsonObjectMain = JsonObject()

            //var jsonObjectVisitorMain = JsonObject()
            var jsonObjectVisitor = JsonObject()
            var visitor_id: String = visitorID.toString()
            jsonObjectVisitor.addProperty("id", visitor_id)
            jsonObjectMain.add("visitor", jsonObjectVisitor)

            //Data Object..Start
            var jsonObjectData = JsonObject()
            jsonObjectData.addProperty("actualEntryTime", formattedDate)

            //Employee Object..Start
            var jsonObjectEmployee = JsonObject()
            jsonObjectEmployee.addProperty("name", visitorName)
            jsonObjectData.add("employee", jsonObjectEmployee)

            jsonObjectMain.add("data", jsonObjectData)

*/
            var jsonObjectMain = JsonObject()

            var visitorJsonObject = JsonObject()
            visitorJsonObject.addProperty("id",visitorID)
            jsonObjectMain.add("visitor",visitorJsonObject)

            var visitingToJsonObject = JsonObject()
            visitingToJsonObject.addProperty("id",visitingToId)
            jsonObjectMain.add("visitingTo",visitingToJsonObject)

            var c = Calendar.getInstance().time
            System.out.println("Current time => " + c)
            /*var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'", Locale.getDefault())

            val timeZone: TimeZone = TimeZone.getTimeZone("IST")
            df.timeZone  = timeZone*/
            var df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            var formattedActualDate: String = df.format(c)
            formattedActualDate = GlobalMethods.convertISTToUTCDate(formattedActualDate)

            var dataJsonObject = JsonObject()
            dataJsonObject.addProperty("actualEntryTime",formattedActualDate)
            dataJsonObject.addProperty("purpose",purpose)
            dataJsonObject.addProperty("comingFrom",comingFrom)
            dataJsonObject.add("images",jsonArray)
            jsonObjectMain.add("data",dataJsonObject)


            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            Log.e("VisitorEntryId:Photo: ",visitorEntryId.toString())
//            MyProgressDialog.showProgressDialog(context!!)
            mAPIService!!.visitorEntryRegister(
                AppConstants.BEARER_TOKEN + SessionManager.getToken(context!!),
                "application/json",
                visitorEntryId,
                jsonObjectMain
            )
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {

                                if (imgCount == 1) {
                                    cameraSource!!.stop()
                                    cameraSource!!.release()
                                    cameraSource = null

                                    val builder = AlertDialog.Builder(context!!)
                                    builder.setCancelable(false)
                                    var message = "Welcome " + visitorName + ", You have been registered"
                                    builder.setMessage(message)
                                    builder.setPositiveButton("Ok") { dialog, which ->
                                        try {
                                            dialog.dismiss()

                                            var intent = Intent(
                                                this@AddVisitorPhotoActivity,
                                                DashboardActivity::class.java
                                            )
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                            /* val intent =
                                                 Intent(applicationContext, DashboardActivity::class.java)
                                             intent.flags =
                                                 Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                             startActivity(intent)*/
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                    val dialog = builder.create()
                                    dialog.show()
                                }


                            } else {
                                /*   SnackBar.showError(
                                       context!!,
                                       snackbarView!!,
                                       "Something went wrong"
                                   )*/
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

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

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

    override fun onBackPressed() {
        finish()
    }
}
