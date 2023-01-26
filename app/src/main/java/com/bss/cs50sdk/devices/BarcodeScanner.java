package com.bss.cs50sdk.devices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.util.Log;
import android.view.WindowManager;

import com.bss.cs50sdk.MainActivity;


public class BarcodeScanner {

    public static BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    public void startScan() {
        Intent intent = new Intent("ACTION_BAR_TRIGSCAN");
        MainActivity.activity.sendBroadcast(intent);
        Log.e("scan","Barcode.startscan");
    }

    public void initScanner(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("broadcast","fds");
                try {
                    String scannedCode = intent.getStringExtra("EXTRA_SCAN_DATA").trim();
                }catch(Exception e){
                    Log.e("scancheckerr",e.getMessage());
                }
            }
        };

        intentFilter = new IntentFilter("ACTION_BAR_SCAN");
        MainActivity.activity.registerReceiver(broadcastReceiver,intentFilter);
    }

    public void register(){
        MainActivity.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intentEnableScan = new Intent("ACTION_BAR_SCANCFG");
        intentEnableScan.putExtra("EXTRA_SCAN_POWER", 1);
        intentEnableScan.putExtra("EXTRA_SCAN_AUTOENT", 0);
        intentEnableScan.putExtra("EXTRA_SCAN_MODE", 3);
        MainActivity.activity.sendBroadcast(intentEnableScan);
    }

    public void unregister(BroadcastReceiver receiver){
        MainActivity.activity.unregisterReceiver(receiver);
        //Restore scan Settings
        Intent intentRestoreScan = new Intent("ACTION_BAR_SCANCFG");
        intentRestoreScan.putExtra("EXTRA_TRIG_MODE", 0);
        intentRestoreScan.putExtra("EXTRA_SCAN_AUTOENT", 1);
        intentRestoreScan.putExtra("EXTRA_SCAN_MODE", 1);
        MainActivity.activity.sendBroadcast(intentRestoreScan);
    }

}
