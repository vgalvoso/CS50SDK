package com.bss.cs50sdk.devices;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.ctk.sdk.PosApiHelper;

import java.util.Arrays;

public class MCR extends Activity {
    PosApiHelper posApiHelper = PosApiHelper.getInstance();
    MCR_Thread mcr_thread;
    public void start(){
        mcr_thread = new MCR_Thread();
        mcr_thread.start();
    }

    public void close(){
        mcr_thread.interrupt();
        posApiHelper.McrClose();
    }

    public class MCR_Thread extends Thread{

        public byte track1[] = new byte[250];
        public byte track2[] = new byte[250];
        public byte track3[] = new byte[250];
        int ret;

        public void run(){
            synchronized(this){
                while(true) {
                    ret = posApiHelper.McrOpen();
                    Log.e("open",ret+"");
                    ret = posApiHelper.McrReset();
                    Log.e("reset",ret+"");
                    int temp = -1;
                    while(temp != 0){   //C1 05
                        try {
                            temp=posApiHelper.McrCheck();
                            Log.e("liuhao", "Lib_McrCheck ="+temp);
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("check",ret+"");
                    Arrays.fill(track1, (byte) 0x00);
                    Arrays.fill(track2, (byte) 0x00);
                    Arrays.fill(track3, (byte) 0x00);
                    ret = posApiHelper.McrRead((byte) 0, (byte) 0, track1, track2, track3);  //c1  07
                    Log.e("read",ret+"");
                    if (ret > 0) {
                        Message msg = new Message();
                        Bundle b = new Bundle();
                        String string = "";
                        Log.d("", "ret = " + ret);
                        if (ret <= 7) {
                            if ((ret & 0x01) == 0x01) {
                                string = "track1:" + new String(track1).trim();
                            }
                            if ((ret & 0x02) == 0x02) {
                                string = string + "\n\ntrack2:" + new String(track2).trim();
                            }
                            if ((ret & 0x04) == 0x04) {
                                string = string + "\n\ntrack3:" + new String(track3).trim();
                            }
                        } else {
                            string = "Lib_MsrRead check data error";
                        }
                        final String str = string;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String result = str;
                                Log.e("Msr", result);
                                if(!result.equals(""))
                                {
                                    String customerCode = result.substring(result.indexOf("A")+1);
                                    customerCode = customerCode.substring(0,customerCode.indexOf("?"));
                                }
                            }
                        });
                        posApiHelper.SysBeep();
                    }
                }
            }
        }

    }

}
