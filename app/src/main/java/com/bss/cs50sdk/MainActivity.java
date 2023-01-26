package com.bss.cs50sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;

import com.bss.cs50sdk.devices.*;

public class MainActivity extends AppCompatActivity {

    public static Activity activity;
    //devices
    BarcodeScanner barcodeScanner;
    NFCAdapter nfcAdapter;
    MCR mcr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.activity = this;

        barcodeScanner = new BarcodeScanner();
        barcodeScanner.initScanner();
        nfcAdapter= new NFCAdapter();
        nfcAdapter.start();
        mcr = new MCR();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barcodeScanner.unregister(BarcodeScanner.broadcastReceiver);
        mcr.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeScanner.register();
        mcr.start();
    }
}