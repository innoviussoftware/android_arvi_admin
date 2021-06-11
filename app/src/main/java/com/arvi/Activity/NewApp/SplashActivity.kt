package com.arvi.Activity.NewApp

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.R
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants.BASE_URL
import com.arvi.Utils.CacheUtils
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import io.fabric.sdk.android.Fabric
import java.io.File
import java.util.*


class SplashActivity : AppCompatActivity() {

    var context: Context? = null
    var MY_PERMISSIONS_REQUEST_ACCOUNTS: Int = 11001


    //todo:: restart app code
    val delayMillis: Long = 1000
    var h: Handler? = null
    var r: Runnable? = null
    var snackbarView:View?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseApp.initializeApp(this@SplashActivity)
        val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(true) // Enables Crashlytics debugger
            .build()
        Fabric.with(fabric)




        try {
            context = SplashActivity@ this
            snackbarView = findViewById(android.R.id.content)
            //todo:: priyanka 31/11/2020
            //todo:: delete photos after this time
            setConnectionSpeedHandlerData()
            setHandlerData()

            //  val dir = File(Environment.getExternalStorageDirectory().toString() + "/Arvi")
            val dir = File(
                context!!.filesDir, "/Arvi"
            )
            if (dir.isDirectory()) {
                val children: Array<String> = dir.list()
                for (i in children.indices) {
                    Log.e("delete:", children[i].toString())
                    File(dir, children[i]).delete()
                }
            }
            //todo:: clear app cache
            try {
                val dir = context!!.getCacheDir()
                deleteDir(dir)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
/*
            try {
                if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                    (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData() // note: it has a return value!
                } else {
                    val packageName =
                        applicationContext.packageName
                    val runtime = Runtime.getRuntime()
                    runtime.exec("pm clear $packageName")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
*/

            //todo:: restart app code end


            /*     if (allPermissionsGranted()) {
                     gotoNextPage()
                 } else {
                     getRuntimePermissions()
                 }*/


        } catch (e: Exception) {
            e.printStackTrace()
            Crashlytics.logException(e.cause)
        }
    }

    fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

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
                            Toast.makeText(context,"Your internet speed is slow",Toast.LENGTH_SHORT).show()
                           */
/* SnackBar.showNetSpeedError(context!!,snackbarView!!,"Your internet speed is slow")*//*

                        }
                    }
                    h!!.postDelayed(this, 60000)
                }
            }

            h!!.post(r as Runnable)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
*/
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
                    Log.e("time:", currenttime)
                    if (currenttime.equals("12 : 00 : 00") || currenttime.equals("23 : 59 : 59")) {
                        Log.e("restart", "true")
                        //restarting the activity
/*                        val dir = File(
                            Environment.getExternalStorageDirectory().toString() + "/Arvi"
                        )*/
                        //todo:: delete photos
                        val dir = File(
                            context!!.filesDir, "/Arvi"
                        )
                        if (dir.isDirectory()) {
                            val children: Array<String> = dir.list()
                            for (i in children.indices) {
                                Log.e("delete:", children[i].toString())
                                File(dir, children[i]).delete()
                            }
                        }
                    }

                    if(currenttime.equals("03 : 01 : 00")||currenttime.equals("06 : 01 : 00")||currenttime.equals("09 : 01 : 00")||
                        currenttime.equals("16 : 45 : 00")||
                        currenttime.equals("12 : 01 : 00")|| currenttime.equals("15 : 01 : 00")||
                        currenttime.equals("18 : 01 : 00")|| currenttime.equals("21 : 01 : 00")||currenttime.equals("23 : 59 : 59")){
                        //todo:: clear app cache
                        try {
                            Log.e("restart", "cleared cache")
                            val dir = context!!.getCacheDir()
                            deleteDir(dir)
                          /*  var size = CacheUtils.getTotalCacheSize(context!!)
                           Log.e("size: ",size)*/


                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }


                    h!!.postDelayed(this, delayMillis)
                }
            }

            h!!.post(r as Runnable)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun gotoNextPage() {
        try {

            if (SessionManager.getIsUserLoggedin(context!!)) {


                BASE_URL = SessionManager.getSelectedServerURL(context!!)
                if (SessionManager.getSelectedDefaultScreen(context!!)
                        .equals(resources.getString(R.string.page_dashboard))
                ) {
                    var intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("from", "splash")
                    startActivity(intent)
                } else if (SessionManager.getSelectedDefaultScreen(context!!)
                        .equals(resources.getString(R.string.page_selfiCheckIn))
                ) {
                    var intent = Intent(context, SelfiCheckInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                } else {
                    var intent = Intent(context, DashboardActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.putExtra("from", "splash")
                    startActivity(intent)
                }
            } else {
                var intent = Intent(context, EnterCompanyDetailActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
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
            Log.i("a", "Permission granted: $permission")
            return true
        }
        Log.i("a", "Permission NOT granted: $permission")
        return false
    }

    private fun getRequiredPermissions(): Array<String> {
        try {
            val info = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            return if (ps != null && ps.size > 0) {
                return ps
            } else {
                return emptyArray()
            }
        } catch (e: Exception) {
            return emptyArray()
        }

    }

    private fun getRuntimePermissions() {
        try {
            val allNeededPermissions = java.util.ArrayList<String>()
            for (permission in getRequiredPermissions()) {
                if (!isPermissionGranted(this, permission)) {
                    allNeededPermissions.add(permission)
                }
            }

            if (!allNeededPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toTypedArray(), MY_PERMISSIONS_REQUEST_ACCOUNTS
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_ACCOUNTS -> {
                try {
                    gotoNextPage()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (allPermissionsGranted()) {

                gotoNextPage()
            } else {
                getRuntimePermissions()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
