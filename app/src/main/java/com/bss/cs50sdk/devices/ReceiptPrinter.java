package com.bss.cs50sdk.devices;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ctk.sdk.PosApiHelper;

public class ReceiptPrinter {

    private static PosApiHelper posApiHelper = PosApiHelper.getInstance();

    public static void printQr(String value){
        print(value,24);
    }

    static Print_Thread printThread = null;
    public static void print(String str,Integer fontSize)
    {
        printThread = new Print_Thread(str, fontSize);
        printThread.start();
    }

    public static class Print_Thread extends Thread {

        private String data;
        private int font;

        public Print_Thread(String str,Integer fontSize){
            data = str;
            font = fontSize;
        }

        public void run() {
            int ret;
            Log.d("Print_Thread[ run ]", "run() begin");
            Message msg = Message.obtain();
            Message msg1 = new Message();

            synchronized (this) {
                int dataSize = data.length();
                int max = 4000;
                if(dataSize > max) {
                    //divide whole string into parts accepted by printer
                    int parts = (int) Math.ceil(dataSize / 4000);
                    int partLen = 0;
                    int newLen = max;
                    String newData = "";
                    for (int part = 0; part <= parts; part++) {
                        partLen = max * part;
                        newLen = (dataSize < (partLen + max)) ? dataSize + 1 : max * (part + 1);
                        Log.e("prints", partLen + " " + newLen + " " + dataSize);
                        newData = data.substring(partLen, newLen - 1);
                        posApiHelper.PrintInit();
                        posApiHelper.PrintSetGray(3);
                        posApiHelper.PrintCheckStatus();
                        posApiHelper.PrintSetFont(Byte.valueOf(Integer.toString(font)),
                                Byte.valueOf(Integer.toString(font)), (byte) 0x00);
                        posApiHelper.PrintStr(newData+"\n\n");

                        SendMsg("Printing... ");
                        ret = posApiHelper.PrintStart();
                        if (ret != 0) {
                            if (ret == -1) {
                                Log.e("Print Status", "No paper");
                                SendMsg("No Paper");
                            } else if (ret == -2) {
                                Log.e("Print Status", "Printer Too Hot ");
                                SendMsg("Printer Too Hot");
                            } else if (ret == -3) {
                                Log.e("Print Status", "Low Battery");
                                SendMsg("Low Battery");
                            } else {
                                Log.e("Print Status", "Print Failed");
                                SendMsg("Print Failed");
                            }
                        }

                        int count = 1;
                        while (ret != 0 && !data.isEmpty()) {
                            Log.e("retry print", Integer.toString(count));
                            SendMsg("Printing... ");
                            ret = posApiHelper.PrintStart();
                            if (ret != 0) {
                                if (ret == -1) {
                                    Log.e("Print Status", "No paper");
                                    SendMsg("No Paper");
                                } else if (ret == -2) {
                                    Log.e("Print Status", "Printer Too Hot ");
                                    SendMsg("Printer Too Hot");
                                } else if (ret == -3) {
                                    Log.e("Print Status", "Low Battery");
                                    SendMsg("Low Battery");
                                } else {
                                    Log.e("Print Status", "Print Failed");
                                    SendMsg("Print Failed");
                                    if (count >= 10)
                                        break;
                                }
                            }
                            count++;
                        }
                    }
                }
                else{
                    posApiHelper.PrintInit();
                    posApiHelper.PrintSetGray(3);
                    posApiHelper.PrintSetFont(Byte.valueOf(Integer.toString(font)),
                            Byte.valueOf(Integer.toString(font)), (byte) 0x00);
                    posApiHelper.PrintStr(data+"\n\n");

                    SendMsg("Printing... ");
                    ret = posApiHelper.PrintStart();
                    if (ret != 0) {
                        if (ret == -1) {
                            Log.e("Print Status", "No paper");
                            SendMsg("No Paper");
                        } else if (ret == -2) {
                            Log.e("Print Status", "Printer Too Hot ");
                            SendMsg("Printer Too Hot");
                        } else if (ret == -3) {
                            Log.e("Print Status", "Low Battery");
                            SendMsg("Low Battery");
                        } else {
                            Log.e("Print Status", "Print Failed");
                            SendMsg("Print Failed");
                        }
                    }

                    int count = 1;
                    while (ret != 0 && !data.isEmpty()) {
                        Log.e("retry print", Integer.toString(count));
                        SendMsg("Printing... ");
                        ret = posApiHelper.PrintStart();
                        if (ret != 0) {
                            if (ret == -1) {
                                Log.e("Print Status", "No paper");
                                SendMsg("No Paper");
                            } else if (ret == -2) {
                                Log.e("Print Status", "Printer Too Hot ");
                                SendMsg("Printer Too Hot");
                            } else if (ret == -3) {
                                Log.e("Print Status", "Low Battery");
                                SendMsg("Low Battery");
                            } else {
                                Log.e("Print Status", "Print Failed");
                                SendMsg("Print Failed");
                                if (count >= 10)
                                    break;
                            }
                        }
                        count++;
                    }
                }

                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("zreaderr",e.getMessage());
                }
            }
        }
    }
    public static void SendMsg(String strInfo) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("MSG", strInfo);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String strInfo = b.getString("MSG");
        }
    };

    public static String center(String text, int maxLength){
        String newText = "";
        int length = text.length();
        int spaces = 0;
        if(length<=maxLength){
            spaces = (length - (length%2)) / 2;
        }
        for(int count = 1; count < spaces;count++){
            newText+=" ";
        }
        newText += text.trim();
        for(int count = 1; count < spaces;count++){
            newText+=" ";
        }
        return newText;
    }
}
