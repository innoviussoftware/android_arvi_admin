package com.arvi.Utils

import android.content.Context
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log


object  ConnectivityDetector {

    public lateinit var msContext: Context

    fun ConnectivityDetector(context: Context) {
        this.msContext = context
    }


    fun isConnectingToInternet(context: Context): Boolean {

        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null)
                for (i in info.indices)
                    if (info[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                        /*if(connectivity.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI)
                        {
                            Log.e("NetworkType:","Wifi")
                            return true
                        }else if(connectivity.activeNetworkInfo!!.type == ConnectivityManager.TYPE_MOBILE){
                            Log.e("NetworkType:","mobile data")
                            if(android.net.TrafficStats.getMobileRxBytes()>0) {
                                return true
                            }else{
                                return false
                            }
                        }else {
                            Log.e("NetworkType:", "no internet")
                            return false
                        }*/

                      /*  if(android.net.TrafficStats.getMobileRxBytes()>0) {*/
//                            return true
/*                        }else{
                            return false
                        }*/
                    }
        }
        return false

    }
}