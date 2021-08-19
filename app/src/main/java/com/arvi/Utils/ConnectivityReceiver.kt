package com.arvi.Utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

class ConnectivityReceiver : BroadcastReceiver() {

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null

        fun isConnected(): Boolean {
            val cm = MyApplication.instance!!.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (connectivityReceiverListener != null) {
                connectivityReceiverListener!!.onNetworkConnectionChanged(isConnected)
            }

           /* if(cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_WIFI)
            {
                Log.e("NetworkType:","Wifi")

            }else if(cm.activeNetworkInfo!!.type == ConnectivityManager.TYPE_MOBILE){
                Log.e("NetworkType:","mobile data")
                if(android.net.TrafficStats.getMobileRxBytes()>0) {
                    Log.e("NetworkType:","have mobile data")
                }else{
                    Log.e("NetworkType:","no mobile data")
                }
            }else {
                Log.e("NetworkType:", "no internet")
            }*/
        } catch (e: Exception) {e.printStackTrace()

        }
    }



    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

}