package com.arvi.Utils;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.arvi.R;
import com.google.android.material.snackbar.Snackbar;

public class ConnectivityDetectorJava {

    private Context msContext;

    public ConnectivityDetectorJava(Context context) {
        this.msContext = context;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }


    public static void showInternetError(Context context, View view) {
        Snackbar snackbar = Snackbar.make(view, R.string.check_internet, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setDuration(10000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.BLACK);
        snackbar.show();
    }

}
