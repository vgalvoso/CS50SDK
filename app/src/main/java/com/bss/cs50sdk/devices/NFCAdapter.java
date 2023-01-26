package com.bss.cs50sdk.devices;

import android.app.Activity;
import android.util.Log;

import com.ctk.sdk.ByteUtil;
import com.ctk.sdk.PosApiHelper;

public class NFCAdapter extends Activity{

    NFC_Thread nfc_thread;
    private static String strID;

    public void start(){
        nfc_thread = new NFC_Thread();
        nfc_thread.start();
    }

    public class NFC_Thread extends Thread {

        PosApiHelper posApiHelper = PosApiHelper.getInstance();
        int ret;

        public NFC_Thread(){

        }

        public void run(){
            synchronized (this){
                try {
                    while (true) {
                        ret = posApiHelper.PiccOpen();
                        final byte CardType[] = new byte[4];
                        final byte UID[] = new byte[10];
                        final byte ucUIDLen[] = new byte[1];
                        final byte ATS[] = new byte[40];
                        final byte ucATSLen[] = new byte[1];
                        final byte SAK[] = new byte[1];

                        if (ret == 0) {
                            long time = System.currentTimeMillis();
                            while (System.currentTimeMillis() < time + 10000) {

                                Log.e("liuhao ", "NFC = " + System.currentTimeMillis());

                                ret = posApiHelper.PiccPolling(CardType, UID, ucUIDLen, ATS, ucATSLen, SAK);

                                if (ret == 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            strID = getLong(ByteUtil.stringToBytes(ByteUtil.bytearrayToHexString(UID, ucUIDLen[0]))) + "";
                                        }
                                    });
                                }
                            }
                        }
                    }
                }catch(Exception e){
                    Log.e("customer tap card",e.getMessage());
                }
            }
        }
    }

    public long getLong(byte[] buf){
        long l = ((buf[0] & 0xFFL) << 24) |
                ((buf[1] & 0xFFL) << 16) |
                ((buf[2] & 0xFFL) << 8) |
                ((buf[3] & 0xFFL) << 0) ;
        return l;
    }

}
