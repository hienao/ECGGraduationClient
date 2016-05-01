package cn.edu.tjut.ecggraduationproject.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.socks.library.KLog;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.abslistview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.model.YangbenWaveInfo;
import cn.edu.tjut.ecggraduationproject.utils.GetWaveDataFromFile;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.edu.tjut.ecggraduationproject.utils.TimeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/4/21 0021.
 */
public class LvYangbenAdapter extends CommonAdapter<YangbenWaveInfo> {
    private List<YangbenWaveInfo> mdatas;
    private Context mContext;
    private int mlayoutId;
    private SweetAlertDialog mPDialog;
    private PreferenceUtils mPreferenceUtils;
    private UserInfo mUserInfo;
    public LvYangbenAdapter(Context context, int layoutId, List<YangbenWaveInfo> datas, UserInfo userInfo) {
        super(context, layoutId, datas);
        this.mUserInfo=userInfo;
        this.mdatas=datas;
        this.mContext=context;
        this.mlayoutId=layoutId;
        mPreferenceUtils=new PreferenceUtils();
    }
    private static final int GETUSERLIST = 0x011;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETUSERLIST:
                    HttpParams mParams1 = new HttpParams();
                    mParams1.put("userid", String.valueOf(mUserInfo.getUserid()));
                    RxVolley.post(MyApplication.HOST+"servlet/sgetAllYangbenWaveDataByUserId", mParams1, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            mdatas.clear();
                            mdatas.addAll( MyJson.json2YangbenWaveInfoList(t));
                            notifyDataSetChanged();
                        }
                    });
                    mPDialog.dismiss();
                    break;

            }
        }
    };

    @Override
    public void convert(ViewHolder holder, final YangbenWaveInfo yangbenWaveInfo) {
        final LinearLayout linearLayout=holder.getView(R.id.ll_yangben_item);
        TextView tv_yangbentime=holder.getView(R.id.tv_yangben_time);
        com.github.mikephil.charting.charts.LineChart lineChart=holder.getView(R.id.chart_yangben);
        Button btn_yangben_del=holder.getView(R.id.btn_yangben_del);
        tv_yangbentime.setText(TimeUtils.millisToLifeString(yangbenWaveInfo.getTime()));
        lineChart.setDescription("");
        lineChart.setDrawGridBackground(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(15f);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(15f);
        float []data=null;
        KLog.v(MyApplication.TAG,yangbenWaveInfo.getData());
        data=MyJson.json2FloatArray(yangbenWaveInfo.getData());
        lineChart.setData(setData(data));
        lineChart.animateY(700, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
        btn_yangben_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("您确定要删除该样本?")
                        .setContentText("删除后不可恢复，请谨慎！")
                        .setCancelText("不删除")
                        .setConfirmText("删除").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                HttpParams mParams = new HttpParams();
                                mParams.put("yangbenid", String.valueOf(yangbenWaveInfo.getWaveid()));
                                RxVolley.post(MyApplication.HOST+"servlet/sdeleteYangbenWave", mParams, new HttpCallback() {
                                    @Override
                                    public void onSuccess(String t) {
                                        int result=0;
                                        if (!t.isEmpty())
                                            result= Integer.parseInt(t.replaceAll("\\D+","").replaceAll("\r", "").replaceAll("\n", "").trim());
                                        if (result==1)
                                            handler.sendEmptyMessageDelayed(GETUSERLIST, 100);
                                    }
                                });
                            }
                        });
                mPDialog.show();
            }
        });
    }
    public LineData setData(float[] dataFiltered) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < dataFiltered.length; i++) {
            xVals.add((i) + "");
        }
        float[]rPeak=null;
        GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(mContext);
        rPeak=getWaveDataFromFile.getDataRPeak(dataFiltered);
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        ArrayList<Entry> rVals = new ArrayList<Entry>();
        for (int i = 0; i < dataFiltered.length; i++) {
            float val = dataFiltered[i];
            for (int j = 0; j < rPeak.length; j++) {
                if (i == (int) rPeak[j]) {
                    rVals.add(new Entry(val, i));
                    j = rPeak.length + 1;
                }

            }
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "心电数据");
        LineDataSet set2 = new LineDataSet(rVals, "R点数据");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);
        set1.setColor(Color.RED);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.fade_red);
        set1.setFillDrawable(drawable);
        set1.setDrawFilled(false);
        set1.setDrawValues(!set1.isDrawValuesEnabled());
        set1.setDrawCircles(false);

        set2.setColor(Color.TRANSPARENT);
        set2.setCircleColor(Color.GREEN);
        set2.setLineWidth(1f);
        set2.setCircleRadius(3f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        Drawable drawable1 = ContextCompat.getDrawable(mContext, R.drawable.fade_red);
        set2.setFillDrawable(drawable1);
        set2.setDrawValues(!set1.isDrawValuesEnabled());
        set2.setDrawFilled(false);
        set2.setDrawCircles(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        return data;
    }
}
