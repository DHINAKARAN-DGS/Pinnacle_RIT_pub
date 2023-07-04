package com.ritchennai.pinnacle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (!(activeNetworkInfo != null && activeNetworkInfo.isConnected())) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setTitle("Internet Connection Required!");
            builder1.setMessage("Turn on the internet connection");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Turn on",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            context.startActivity(i);
                        }
                    });

            builder1.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }
}
