package com.arvi.Utils

import android.app.Application


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    companion object {

        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }
}