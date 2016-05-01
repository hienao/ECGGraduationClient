package cn.edu.tjut.ecggraduationproject.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.task.GetBlueToothECGDataTask;
import cn.edu.tjut.ecggraduationproject.utils.GetWaveDataFromFile;
import cn.edu.tjut.ecggraduationproject.utils.GsonUtils;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CollectDataActivity extends AppCompatActivity {

    private GetBlueToothECGDataTask getBlueToothECGDataTask;
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static BluetoothSocket btSocket;
    private BluetoothAdapter btAdapt;
    private String ECGDeviceName = "HMSoft";
    private String ECGDeviceAddress;
    private boolean ECGDeviceFlag = false;
    public static MyApplication mApplication;
    private boolean btAdaptStartDiscoveryFlag = false;
    private int dataBytes;
    private Timer timer = new Timer();
    private PreferenceUtils preferenceUtils;
    private Button btn_collect_data;
    private Button btn_connect_device;
    private TextView tv_collect_state;
    private ProgressBar progressBarHorizontal;


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CollectDataActivity.this);
                //				builder.setIcon(R.drawable.toast_icon);
                builder.setTitle("心电监测系统");
                builder.setMessage("没法发现任何蓝牙设备，请确定是否开启心电采集设备的蓝牙？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.create();
                builder.show();
            }
            super.handleMessage(msg);
        }
    };
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            if (btAdaptStartDiscoveryFlag && btn_connect_device.getText().equals("连接心电设备"))
                message.what = 1;
            handler.sendMessage(message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);
        tv_collect_state = (TextView) findViewById(R.id.tv_collect_state);
        btn_connect_device = (Button) findViewById(R.id.btn_connect_device);
        btn_collect_data = (Button) findViewById(R.id.btn_collect_data);
        progressBarHorizontal = (ProgressBar) findViewById(R.id.draft_progress_bar);
        mApplication = (MyApplication) this.getApplication();
        //获取采集蓝牙字节数设置
        preferenceUtils=new PreferenceUtils();
        dataBytes = preferenceUtils.getPreferenceInt("dataBytes", 50000);
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册Receiver来获取蓝牙设备相关的结果
        registerReceiver(searchDevices, intent);

        // 初始化本机蓝牙功能
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
            btAdapt.enable();
            mApplication.setBtSocketConnectFlag(false);
            //			Toast.makeText(CollectDataActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
        }
        if (mApplication.getBtSocketConnectFlag()) {
            btn_collect_data.setVisibility(View.VISIBLE);
            btn_connect_device.setText("断开连接");
            tv_collect_state.setText("蓝牙连接成功！");
        } else {
            btAdaptStartDiscoveryFlag = btAdapt.startDiscovery();
        }
        timer.schedule(task, 10000);

        btn_connect_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getBtSocketConnectFlag()) {
                    try {
                        CollectDataActivity.btSocket.close();
                        mApplication.setBtSocketConnectFlag(false);
                        btn_connect_device.setText("点击重新连接");
                        btn_collect_data.setVisibility(View.GONE);
                        tv_collect_state.setText("蓝牙连接已断开！");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    tv_collect_state.setText("正在连接蓝牙...");
                    btAdapt.startDiscovery();
                }
            }
        });
        btn_collect_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getBtSocketConnectFlag()) {
                    tv_collect_state.setText("采集中......");

                    //String [] params = {fi.toString()};
                    //progressDialog = CustomProgressDialog.createDialog(CollectDataActivity.this);
                    getBlueToothECGDataTask = new GetBlueToothECGDataTask(CollectDataActivity.this, progressBarHorizontal,dataBytes);
                    try {
                        String filepath=getBlueToothECGDataTask.execute().get();
                        GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(CollectDataActivity.this);
                        float[]data=getWaveDataFromFile.getDataFilteredFromPath(filepath);
                        long time=getWaveDataFromFile.getTimeFromPath(filepath);
                        String wavegson= GsonUtils.floatArray2Json(data);
                        HttpParams params = new HttpParams();
                        params.put("data",wavegson);//文件上传
                        RxVolley.post(MyApplication.HOST + "servlet/sRecognizeRaw", params, new HttpCallback() {
                            @Override
                            public void onSuccess(String t) {
                                UserInfo userInfo= MyJson.json2User(t);
                                if (userInfo.getUserid()>0){
                                    preferenceUtils.setPreferenceString("user",t);
                                    Intent intent=new Intent(CollectDataActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                            @Override
                            public void onFailure(int errorNo, String strMsg) {
                                super.onFailure(errorNo, strMsg);
                                SweetAlertDialog pDialog = new SweetAlertDialog(CollectDataActivity.this);
                                pDialog.setTitleText("识别结果");
                                pDialog.setContentText("识别失败 " + errorNo + ":" + strMsg);
                                pDialog.setConfirmText("确定");
                                pDialog.show();
                            }
                        });
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.dialog_recognize_result, null);
                        TextView ad_tv_content= (TextView) view.findViewById(R.id.ad_tv_content);
                        final EditText ad_et_username= (EditText) view.findViewById(R.id.ad_et_username);
                        Button ad_btn_recollect= (Button) view.findViewById(R.id.ad_btn_recollect);
                        Button ad_btn_confirm= (Button) view.findViewById(R.id.ad_btn_confirm);
                        ad_btn_confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String ad_et=ad_et_username.getText().toString();
                                HttpParams params = new HttpParams();
                                params.put("name",ad_et);
                                RxVolley.post(MyApplication.HOST + "servlet/sisNameExit", params, new HttpCallback() {
                                    @Override
                                    public void onSuccess(String t) {
                                        if (t.equals("true")){
                                            HttpParams params = new HttpParams();
                                            params.put("name",t);
                                            RxVolley.post(MyApplication.HOST + "servlet/sgetUserIdByName", params, new HttpCallback() {
                                                @Override
                                                public void onSuccess(String t) {
                                                    final String tempuserid=t;
                                                    HttpParams params = new HttpParams();
                                                    params.put("userid",t);
                                                    RxVolley.post(MyApplication.HOST + "servlet/sgetAllYangbenWaveDataByUserId", params, new HttpCallback() {
                                                        @Override
                                                        public void onSuccess(String t) {
                                                            if (t.equals("[]")){
                                                                HttpParams params = new HttpParams();
                                                                params.put("userid",tempuserid);
                                                                RxVolley.post(MyApplication.HOST + "servlet/sgetUserInfoById", params, new HttpCallback() {
                                                                    @Override
                                                                    public void onSuccess(String t) {
                                                                        preferenceUtils.setPreferenceString("user",t);
                                                                        startActivity(new Intent(CollectDataActivity.this,HomeActivity.class));
                                                                        finish();
                                                                    }

                                                                    @Override
                                                                    public void onFailure(int errorNo, String strMsg) {
                                                                        super.onFailure(errorNo, strMsg);
                                                                        SweetAlertDialog pDialog = new SweetAlertDialog(CollectDataActivity.this);
                                                                        pDialog.setTitleText("故障");
                                                                        pDialog.setContentText("故障 " + errorNo + ":" + strMsg);
                                                                        pDialog.setConfirmText("确定");
                                                                        pDialog.show();
                                                                    }
                                                                });

                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(int errorNo, String strMsg) {
                                                            super.onFailure(errorNo, strMsg);
                                                            SweetAlertDialog pDialog = new SweetAlertDialog(CollectDataActivity.this);
                                                            pDialog.setTitleText("故障");
                                                            pDialog.setContentText("故障 " + errorNo + ":" + strMsg);
                                                            pDialog.setConfirmText("确定");
                                                            pDialog.show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(int errorNo, String strMsg) {
                                                    super.onFailure(errorNo, strMsg);
                                                    SweetAlertDialog pDialog = new SweetAlertDialog(CollectDataActivity.this);
                                                    pDialog.setTitleText("故障");
                                                    pDialog.setContentText("故障 " + errorNo + ":" + strMsg);
                                                    pDialog.setConfirmText("确定");
                                                    pDialog.show();
                                                }
                                            });
                                        }
                                        else {
                                            SweetAlertDialog pDialog = new SweetAlertDialog(CollectDataActivity.this);
                                            pDialog.setTitleText("不存在该用户");
                                            pDialog.setContentText("请检查用户名是否输入错误");
                                            pDialog.setConfirmText("确定");
                                            pDialog.show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int errorNo, String strMsg) {
                                        super.onFailure(errorNo, strMsg);
                                        SweetAlertDialog pDialog = new SweetAlertDialog(CollectDataActivity.this);
                                        pDialog.setTitleText("故障");
                                        pDialog.setContentText("故障 " + errorNo + ":" + strMsg);
                                        pDialog.setConfirmText("确定");
                                        pDialog.show();
                                    }
                                });
                            }
                        });
                        AlertDialog.Builder builder = new AlertDialog.Builder(CollectDataActivity.this);
                        builder.setView(view);
                        builder.setTitle("识别结果");
                        builder.create().show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

    }

    private final BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            Log.e("", "---- 广播响应！");
            String action = intent.getAction();
            //			mTextView.setText("蓝牙连接中......");
            //搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("device.getName()", "---------      " + device.getName());
                if (device.getName() != null)
                    if (device.getName().equals(ECGDeviceName)) {
                        ECGDeviceAddress = device.getAddress();
                        ECGDeviceFlag = true;
                        Log.i("ECGBlueToothDeviceStr", "---------      " + ECGDeviceAddress);
                    }
            } else {
                btn_connect_device.setText("点击重新连接");
                Toast.makeText(CollectDataActivity.this, "没有发现心电采集设备，连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
            }
            connectECGDevice();
        }
    };

    private void connectECGDevice() {
        Log.e("", "---- 准备配对！");
        btAdapt.cancelDiscovery();
        UUID uuid = UUID.fromString(SPP_UUID);
        if (ECGDeviceFlag) {

            BluetoothDevice btDev = btAdapt.getRemoteDevice(ECGDeviceAddress);
            try {
                btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
                btSocket.connect();
                mApplication.setBtSocketConnectFlag(true);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                tv_collect_state.setText("蓝牙连接失败！");
                btn_connect_device.setText("点击重新连接");
                mApplication.setBtSocketConnectFlag(false);
                Toast.makeText(CollectDataActivity.this, "连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
            }
            if (mApplication.getBtSocketConnectFlag()) {
                btn_collect_data.setVisibility(View.VISIBLE);
                btn_connect_device.setText("断开连接");
                tv_collect_state.setText("蓝牙连接成功！");
            } else {
                try {
                    Log.e("", "尝试关闭Socket");
                    btSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(CollectDataActivity.this, "没有发现心电采集设备，连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
        }
    }

}
