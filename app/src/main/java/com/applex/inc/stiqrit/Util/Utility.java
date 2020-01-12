package com.applex.inc.stiqrit.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;


public class Utility {

    /** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT */
    public static boolean checkConnection(@NonNull Context context) {
        return  ((ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public static void showToast(@NonNull Context context, String toast) {
        Toast.makeText(context,toast,Toast.LENGTH_SHORT).show();
    }
}
