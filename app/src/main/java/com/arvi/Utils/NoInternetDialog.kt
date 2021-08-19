package com.arvi.Utils

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.arvi.R
import com.arvi.Utils.MyProgressDialog.hideProgressDialog
import com.arvi.Utils.MyProgressDialog.showProgressDialog

class NoInternetDialog {

    companion object {
        private var tvTryAgain: TextView? = null
        private var handler: Handler? = null
        private var no_internet_dialog: Dialog? = null

        fun checkConnection(context: Context) {

            val isConnected = ConnectivityReceiver.isConnected()
            showSnack(isConnected, context)
        }

        fun showSnack(isConnected: Boolean, context: Context) {
            val message: String
            val color: Int
            if (isConnected) {
                hideDialog(context)
                /* message = "Good! Connected to Internet";
            color = Color.WHITE;*/
            } else {
                showDialog(context)
                /*            message = "Sorry! Not connected to internet";
            color = Color.RED;*/
            }

        }


        fun showDialog(context: Context) {

            try {

                if (no_internet_dialog == null || !no_internet_dialog!!.isShowing) {
                    no_internet_dialog =
                        Dialog(context, android.R.style.Theme_Translucent_NoTitleBar)
                    no_internet_dialog!!.setCancelable(false)
                    no_internet_dialog!!.setContentView(R.layout.dialog_no_internet)

                    tvTryAgain = no_internet_dialog!!.findViewById(R.id.tvTryAgain)
                    tvTryAgain!!.setOnClickListener {
                        progressTimer(context);
                        checkConnection(context)
                    }

                    no_internet_dialog!!.show()
                    val window = no_internet_dialog!!.window
                    window!!.setLayout(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        private fun progressTimer(context: Context) {
            showProgressDialog(context)
            handler = Handler()

            handler!!.postDelayed(Runnable { hideProgressDialog() }, 1000)
        }

        fun hideDialog(context: Context) {
            try {
                if (no_internet_dialog != null && no_internet_dialog!!.isShowing) {
                    no_internet_dialog!!.dismiss()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}

