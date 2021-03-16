package com.arvi.Activity.NewApp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.R
import com.arvi.SessionManager.SessionManager
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import io.fabric.sdk.android.Fabric
import java.util.*


class SplashActivity : AppCompatActivity() {

    var context: Context? = null
    var MY_PERMISSIONS_REQUEST_ACCOUNTS: Int = 11001


    //todo:: restart app code
    val delayMillis: Long = 1000
    var h: Handler? = null
    var r: Runnable? = null

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

            //todo:: priyanka 31/11/2020
            //todo:: restart app code start
//            setHandlerData()
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


    private fun gotoNextPage() {
        try {
            if (SessionManager.getIsUserLoggedin(context!!)) {
                if(SessionManager.getSelectedDefaultScreen(context!!).equals(resources.getString(R.string.page_dashboard))) {
                    var intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("from","splash")
                    startActivity(intent)
                }else if(SessionManager.getSelectedDefaultScreen(context!!).equals(resources.getString(R.string.page_selfiCheckIn))) {
                    var intent = Intent(context, SelfiCheckInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }else {
                    var intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra("from","splash")
                    startActivity(intent)
                }
            }else {
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
