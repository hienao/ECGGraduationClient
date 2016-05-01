package cn.edu.tjut.ecggraduationproject.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kymjs.core.bitmap.client.BitmapCore;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.socks.library.KLog;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.fragement.CollectDataFragment;
import cn.edu.tjut.ecggraduationproject.fragement.FileFragment;
import cn.edu.tjut.ecggraduationproject.fragement.OnlineFileFragment;
import cn.edu.tjut.ecggraduationproject.fragement.SettingFragment;
import cn.edu.tjut.ecggraduationproject.fragement.SingleWaveFragment;
import cn.edu.tjut.ecggraduationproject.fragement.TestUserFragment;
import cn.edu.tjut.ecggraduationproject.fragement.UserInfoEditFragment;
import cn.edu.tjut.ecggraduationproject.fragement.YangbenFragment;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private PreferenceUtils preferenceUtils;
    private UserInfo userInfo;
    HttpCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //header设置
        View headerView = navigationView.getHeaderView(0);
        ImageView nv_iv_user_photo = (ImageView) headerView.findViewById(R.id.iv_user_photo);
        TextView nv_tv_username = (TextView) headerView.findViewById(R.id.tv_username);
        TextView nv_tv_userphone = (TextView) headerView.findViewById(R.id.tv_userphone);
        navigationView.setNavigationItemSelectedListener(this);
        preferenceUtils=new PreferenceUtils();
        /**
         * 这里联网检测用户样本库中是否有样本，若没有则弹窗提示
         *
         * */
        userInfo=MyJson.json2User(preferenceUtils.getPreferenceString("user"));
        HttpParams mParams = new HttpParams();
        KLog.v(MyApplication.TAG,userInfo.toString());
        mParams.put("userid", String.valueOf(userInfo.getUserid()));
        RxVolley.post(MyApplication.HOST+"servlet/sgetAllYangbenWaveDataByUserId", mParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("[]")||t.isEmpty()){
                    SweetAlertDialog mPDialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("警告")
                            .setContentText("当前用户样本库中无样本数据，若退出前不添加样本数据，此账户将作废")
                            .setConfirmText("确定");
                    mPDialog.show();
                }
            }
        });
        nv_tv_username.setText(userInfo.getName());
        nv_tv_userphone.setText(String.valueOf(userInfo.getPhone()));
        new BitmapCore.Builder()
                .url(userInfo.getPhotopath())
                .callback(callback)
                .view(nv_iv_user_photo)
                .loadResId(R.mipmap.ic_launcher)
                .errorResId(R.mipmap.ic_launcher)
                .doTask();

        int id = getIntent().getIntExtra("id" , 0);//默认0打开用户信息fragment
        String yangbenwavelisgson= getIntent().getStringExtra("yangbenwavelisgson");
        if (id==1){
            setTitle("单波列表");
            SingleWaveFragment singleWaveFragment=new SingleWaveFragment();
            FragmentManager fragmentManager =getFragmentManager();
            FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,singleWaveFragment);
            fragmentTransaction.commit();
            singleWaveFragment.isloadfromlocalfile(true);//从本地文件加载时不显示删除
            singleWaveFragment.getsinglewavedata(yangbenwavelisgson);
        }else {

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id==R.id.nav_collectdata){
            setTitle("数据采集");
            CollectDataFragment collectDataFragment=new CollectDataFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,collectDataFragment);
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_user_info) {
            setTitle("用户信息管理");
            UserInfoEditFragment userInfoEditFragment=new UserInfoEditFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,userInfoEditFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_ecg_info) {
            setTitle("心电数据管理");
            OnlineFileFragment onlineFileFragment=new OnlineFileFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,onlineFileFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_sample_wave) {
            setTitle("样本波形管理");
            YangbenFragment yangbenFragment=new YangbenFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,yangbenFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_local_files) {
            setTitle("本地心电文件管理");
            FileFragment fileFragment=new FileFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,fileFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_setting) {
            setTitle("系统设置");
            SettingFragment settingFragment=new SettingFragment();
            android.support.v4.app.FragmentManager fragmentManager=getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,settingFragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_test){
            setTitle("测试用户设置");
            TestUserFragment testUserFragment=new TestUserFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fl_content,testUserFragment);
            fragmentTransaction.commit();
        }else if (id == R.id.nav_exit){
            this.finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
