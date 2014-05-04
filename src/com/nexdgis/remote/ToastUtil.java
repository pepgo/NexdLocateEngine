package com.nexdgis.remote;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {
    Handler mHandler;
    Context context;
    public ToastUtil(Context context) {
        mHandler = new Handler(Looper.getMainLooper());
        this.context = context;
    }
    class MyRunnable implements Runnable {
        private String contentString = null;
        private int contentId = 0;
        private boolean isString = false;
        public MyRunnable(int contentId) {
            super();
            this.contentId = contentId;
            isString = false;
        }
        public MyRunnable(String contentString) {
            super();
            this.contentString = contentString;
            isString = true;
        }
        public void run() {
            // TODO Auto-generated method stub
            if (isString) {
                Toast.makeText(context, contentString,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, contentId,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void showToast(String content) {
        mHandler.post(new MyRunnable(content));
    }
    public void showToast(int contentId) {
        mHandler.post(new MyRunnable(contentId));
    }
} 
