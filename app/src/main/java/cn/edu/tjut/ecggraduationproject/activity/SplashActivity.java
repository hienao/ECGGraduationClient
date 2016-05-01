package cn.edu.tjut.ecggraduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.ProgressListener;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.edu.tjut.ecggraduationproject.utils.StreamUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends Activity {

    private static final int CODE_UPDATE_DIALOG = 0;
    private static final int CODE_URL_ERROR = 1;
    private static final int CODE_NET_ERROR = 2;
    private static final int CODE_JSON_ERROR = 3;
    private static final int CODE_ENTER_HOME = 4;
    private TextView tvVersion, tvDownLoadProgress;
    private String mVersionName, mDescription, mDownloadUrl;//软件版本名称、软件更新描述、软件下载地址
    private int mVersionCode;//软件版本号
    private RelativeLayout rlRoot;// 根布局
    private PreferenceUtils preferenceUtils;
    private UserInfo userInfo;
    private File mSaveFile;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "URL错误", Toast.LENGTH_SHORT).show();
                    enterCollectData();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    enterCollectData();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "JSON解析错误", Toast.LENGTH_SHORT).show();
                    KLog.v(MyApplication.TAG,"JSON解析错误");
                    enterCollectData();
                    break;
                case CODE_ENTER_HOME:
                    enterCollectData();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //设置页面全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvDownLoadProgress = (TextView) findViewById(R.id.tv_downloadprogress);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        tvVersion.setText("版本号：" + getVersionName());
        checkVersion();
        preferenceUtils=new PreferenceUtils();

        // 渐变的动画效果
        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);
        anim.setDuration(2000);
        rlRoot.startAnimation(anim);
    }
    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /*
    * 检测更新
    * */
    private void checkVersion() {
        final long starttime = System.currentTimeMillis();
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(MyApplication.HOST+"update.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = conn.getInputStream();
                        String result = StreamUtils.readFromStream(inputStream);
                        KLog.v(MyApplication.TAG,"网络返回：" + result);
                        //解析json
                        JSONObject jsonObject = new JSONObject(result);
                        mVersionName = jsonObject.getString("versionName");
                        mVersionCode = jsonObject.getInt("versionCode");
                        mDescription = jsonObject.getString("description");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        System.out.println("m:" + mVersionCode + "  " + getVersionCode());
                        if (mVersionCode > getVersionCode()) {//有更新
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            msg.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {//网址错误
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {//网络错误
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {//json解析失败
                    msg.what = CODE_JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endtime = System.currentTimeMillis();
                    long timeused = endtime - starttime;
                    if (timeused < 2000) {
                        try {
                            Thread.sleep(2000 - timeused);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                super.run();
            }
        }.start();
    }

    /*
   * 下载apk
   * */
    private void downloadApk() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String target = this.getExternalFilesDir("apk") + "/update.apk";
            KLog.v(MyApplication.TAG, target);
            mSaveFile = new File(target);
            //下载进度(可选参数，不需要可不传)
            ProgressListener listener = new ProgressListener() {
                @Override
                public void onProgress(long transferredBytes, long totalSize) {
                    tvDownLoadProgress.setText("下载进度：" + (transferredBytes / totalSize) * 100 + "%");
                }
            };
            //下载回调，内置了很多方法，详细请查看源码
            // 包括在异步响应的onSuccessInAsync():注不能做UI操作
            // 下载成功时的回调onSuccess()
            // 下载失败时的回调onFailure():例如无网络，服务器异常等
            HttpCallback callback = new HttpCallback() {
                @Override
                public void onSuccessInAsync(byte[] t) {
                }

                @Override
                public void onSuccess(String t) {
                    Toast.makeText(getBaseContext(), "下载成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(mSaveFile), "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
                }
            };
            RxVolley.download(this.getExternalFilesDir("apk") + "/update.apk",
                    mDownloadUrl,
                    listener, callback);
        } else {
            Toast.makeText(SplashActivity.this, "没有找到SD卡！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterCollectData();
        super.onActivityResult(requestCode, resultCode, data);
    }
    /*
        * 弹出升级对话框
        * */
    private void showUpdateDialog() {
        SweetAlertDialog pDialog = new SweetAlertDialog(this);
        pDialog.setTitleText("最新版本：" + mVersionName);
        pDialog.setContentText("版本描述：" + mDescription);
        pDialog.setConfirmText("立即更新");
        pDialog.setCancelText("以后再说");
        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                tvDownLoadProgress.setVisibility(View.VISIBLE);
                downloadApk();
                KLog.v(MyApplication.TAG, "立即更新");
            }
        });
        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                enterCollectData();
            }
        });
        pDialog.show();
    }

    /*
    * 进入主页面
    * */
    public void enterCollectData() {
       if (!preferenceUtils.getPreferenceBoolean("debugmode",false))
       {
           /**测试用**//*
           UserInfo userInfo=new UserInfo();
           userInfo.setUserid(3);
           preferenceUtils.setPreferenceString("user",MyJson.user2Json(userInfo));*/
           /**测试用**/
           Intent intent = new Intent(this, HomeActivity.class);
           startActivity(intent);
           finish();
       }else {
           String userinfogson=preferenceUtils.getPreferenceString("user");
           if (!userinfogson.isEmpty()){
               userInfo= MyJson.json2User(userinfogson);
               userInfo.setName("检测");
               preferenceUtils.setPreferenceString("user",MyJson.user2Json(userInfo));
           }
           Intent intent = new Intent(this, CollectDataActivity.class);
           startActivity(intent);
           finish();
       }
    }
}
