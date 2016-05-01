package cn.edu.tjut.ecggraduationproject.fragement;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.socks.library.KLog;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.task.GetBlueToothECGDataFragementTask;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link CollectDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectDataFragment extends Fragment {
    @Bind(R.id.tv_collect_state)
    TextView mTvCollectState;
    @Bind(R.id.btn_connect_device)
    Button mBtnConnectDevice;
    @Bind(R.id.btn_collect_data)
    Button mBtnCollectData;
    @Bind(R.id.number_progress_bar)
    NumberProgressBar mNumberProgressBar;
    private GetBlueToothECGDataFragementTask mGetBlueToothECGDataFragementTask;
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static BluetoothSocket btSocket;
    private BluetoothAdapter btAdapt;
    /*private String ECGDeviceName = "HMSoft";*/
    private String ECGDeviceName = "RL-20160331QWZE";
    private String ECGDeviceAddress;
    private boolean ECGDeviceFlag = false;
    public static MyApplication mApplication;
    private boolean btAdaptStartDiscoveryFlag = false;
    private int dataBytes;
    private Timer timer = new Timer();
    private PreferenceUtils preferenceUtils;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            KLog.v(MyApplication.TAG,"btAdaptStartDiscoveryFlag:"+btAdaptStartDiscoveryFlag);
            if (btAdaptStartDiscoveryFlag && mBtnConnectDevice.getText().equals("连接心电设备"))
                message.what = 1;
            handler.sendMessage(message);
        }
    };
    public CollectDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CollectDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CollectDataFragment newInstance(String param1, String param2) {
        CollectDataFragment fragment = new CollectDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils=new PreferenceUtils();
        mApplication = (MyApplication) getActivity().getApplication();
        //获取采集蓝牙字节数设置
        dataBytes = preferenceUtils.getPreferenceInt("dataBytes", 50000);
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 注册Receiver来获取蓝牙设备相关的结果
        getActivity().registerReceiver(searchDevices, intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_data, container, false);
        ButterKnife.bind(this, view);
        // 初始化本机蓝牙功能
        btAdapt = BluetoothAdapter.getDefaultAdapter();
        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
            btAdapt.enable();
            mApplication.setBtSocketConnectFlag(false);
        }
        if (mApplication.getBtSocketConnectFlag()) {
            mBtnCollectData.setVisibility(View.VISIBLE);
            mBtnConnectDevice.setText("断开连接");
            mTvCollectState.setText("蓝牙连接成功！");
        } else {
            btAdaptStartDiscoveryFlag = btAdapt.startDiscovery();
            mTvCollectState.setText("蓝牙尚未连接！");
            mBtnConnectDevice.setText("连接心电设备");
        }
        timer.schedule(task, 10000);
        mBtnConnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getBtSocketConnectFlag()) {
                    try {
                        CollectDataFragment.btSocket.close();
                        mApplication.setBtSocketConnectFlag(false);
                        mBtnConnectDevice.setText("点击重新连接");
                        mBtnCollectData.setVisibility(View.GONE);
                        mTvCollectState.setText("蓝牙连接已断开！");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    mTvCollectState.setText("正在连接蓝牙...");
                    btAdapt.startDiscovery();
                }
            }
        });
        mBtnCollectData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getBtSocketConnectFlag()) {
                    mTvCollectState.setText("采集中......");
                    mGetBlueToothECGDataFragementTask=new GetBlueToothECGDataFragementTask(getActivity(), mNumberProgressBar,dataBytes);
                    mGetBlueToothECGDataFragementTask.execute();

                } else {
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                mBtnConnectDevice.setText("点击重新连接");
                Toast.makeText(getActivity(), "没有发现心电采集设备，连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
            }
            connectECGDevice();
        }
    };
    private void connectECGDevice() {
        KLog.v(MyApplication.TAG,"准备配对");
        btAdapt.cancelDiscovery();
        UUID uuid = UUID.fromString(SPP_UUID);
        if (ECGDeviceFlag) {
            KLog.v(MyApplication.TAG,"获得设备地址:"+ECGDeviceAddress);
            BluetoothDevice btDev = btAdapt.getRemoteDevice(ECGDeviceAddress);
            try {
                btSocket=null;
                /*Method m = btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                btSocket= (BluetoothSocket)m.invoke(btDev, Integer.valueOf(1));*/
                int sdk = Integer.parseInt(Build.VERSION.SDK);
                if (sdk >= 10) {
                    btSocket = btDev.createInsecureRfcommSocketToServiceRecord(uuid);
                } else {
                    btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
                }
                /*btSocket = btDev.createRfcommSocketToServiceRecord(uuid);*/
                btSocket.connect();
                mApplication.setBtSocketConnectFlag(true);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                KLog.v(MyApplication.TAG,"连接错误:"+e.toString());
                mTvCollectState.setText("蓝牙连接失败！");
                mBtnConnectDevice.setText("点击重新连接");
                mApplication.setBtSocketConnectFlag(false);
                Toast.makeText(getActivity(), "连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
            } /*catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }*/
            if (mApplication.getBtSocketConnectFlag()) {
                mBtnCollectData.setVisibility(View.VISIBLE);
                mBtnConnectDevice.setText("断开连接");
                mTvCollectState.setText("蓝牙连接成功！");
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
            Toast.makeText(getActivity(), "没有发现心电采集设备，连接失败，请重新点击连接！", Toast.LENGTH_LONG).show();
        }
    }
}
