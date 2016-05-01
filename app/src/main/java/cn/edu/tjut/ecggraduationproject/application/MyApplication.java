package cn.edu.tjut.ecggraduationproject.application;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.kymjs.okhttp.OkHttpStack;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.socks.library.KLog;
import com.squareup.okhttp.OkHttpClient;

import cn.edu.tjut.ecggraduationproject.BuildConfig;
/**
 * 此类用于全局初始化变量
 * Created by Administrator on 2016/3/10 0010.
 */
public class MyApplication extends Application {
    public static String TAG="SWTTAG";
    /*public static String HOST="http://192.168.1.23:8080/ECGServeltService/";*/
    public static String HOST="http://shiwentao.cn/";
    private  boolean btSocketConnectFlag;
    private static Context context;
    private static BluetoothDevice remoteDevice;//全局蓝牙Devices

    public static BluetoothDevice getRemoteDevice() {
        return remoteDevice;
    }

    public static void setRemoteDevice(BluetoothDevice remoteDevice) {
        MyApplication.remoteDevice = remoteDevice;
    }

    @Override

    public void onCreate() {
        super.onCreate();
        //在App Application中初始化RXvolley
        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(RxVolley.CACHE_FOLDER, new OkHttpStack(new OkHttpClient())));
        //获取Context
        context = getApplicationContext();
        //初始化KLOG
        KLog.init(BuildConfig.LOG_DEBUG);

    }
    //返回
    public static Context getContextObject(){
        return context;
    }
    public boolean getBtSocketConnectFlag() {
        return btSocketConnectFlag;
    }
    public void setBtSocketConnectFlag(boolean btSocketConnectFlag) {
        this.btSocketConnectFlag = btSocketConnectFlag;
    }
}
