package cn.edu.tjut.ecggraduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.FileInfo;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.GetWaveDataFromFile;
import cn.edu.tjut.ecggraduationproject.utils.GsonUtils;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.edu.tjut.ecggraduationproject.utils.ScreenShotUtils;
import cn.edu.tjut.ecggraduationproject.utils.WaveUtils;
import cn.edu.tjut.ecggraduationproject.view.MyMarkerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewWaveActivity extends Activity implements OnChartGestureListener, OnChartValueSelectedListener {

    private float[] dataFiltered=null;
    private float[] rPeak = null;
    private LineChart mChart;
    private Button btn_select_data_start, btn_select_data, btn_select_data_end, btn_show_button, btn_share_screen,btn_upload_file,btn_look_single_wave,btn_recognize;
    private LinearLayout ll_tools;
    private int waveDataSetIndex = -1;//手指光标选中的数据在波形数组中的下标；
    private int select_data_start_index, select_data_end_index = -1;//要截取的波形开始和结束的下标
    private PreferenceUtils preferenceUtils;
    private long time;
    private boolean jiance;//判断是应用启动时的检测、还是应用中的采集

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wave);
        //设置页面全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ll_tools = (LinearLayout) findViewById(R.id.ll_tools);
        btn_select_data_start = (Button) findViewById(R.id.btn_select_data_start);
        btn_select_data_end = (Button) findViewById(R.id.btn_select_data_end);
        btn_select_data = (Button) findViewById(R.id.btn_select_data);
        btn_share_screen = (Button) findViewById(R.id.btn_share_screen);
        btn_show_button = (Button) findViewById(R.id.btn_show_button);
        btn_upload_file = (Button) findViewById(R.id.btn_upload_file);
        btn_look_single_wave = (Button) findViewById(R.id.btn_look_single_wave);
        btn_recognize= (Button) findViewById(R.id.btn_recognize);
        mChart = (LineChart) findViewById(R.id.chart1);
        preferenceUtils=new PreferenceUtils();
        btn_select_data_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waveDataSetIndex != -1)
                    select_data_start_index = waveDataSetIndex;
                waveDataSetIndex = -1;//将临时赋值数据清除
                Toast.makeText(ViewWaveActivity.this, "选取了截取开始点，下标：" + select_data_start_index, Toast.LENGTH_SHORT).show();
            }
        });
        btn_select_data_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waveDataSetIndex != -1)
                    select_data_end_index = waveDataSetIndex;
                waveDataSetIndex = -1;//将临时赋值数据清除
                Toast.makeText(ViewWaveActivity.this, "选取了截取结束点，下标：" + select_data_end_index, Toast.LENGTH_SHORT).show();
            }
        });
        btn_select_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_data_start_index != -1 && select_data_end_index != -1){
                    dataFiltered= WaveUtils.cutWaveData(dataFiltered,select_data_start_index,select_data_end_index);
                    GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(ViewWaveActivity.this);
                    rPeak=getWaveDataFromFile.getDataRPeak(dataFiltered);
                }
                if( (WaveUtils.getWaveDataMax(dataFiltered)-WaveUtils.getWaveDataMin(dataFiltered))<400){
                    YAxis leftAxis = mChart.getAxisLeft();
                    leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
                    leftAxis.setAxisMaxValue(400);
                    leftAxis.setAxisMinValue(-400);
                }
                setData(dataFiltered);
                mChart.invalidate();
                select_data_start_index = -1;
                select_data_end_index = -1;
                Toast.makeText(ViewWaveActivity.this, "开始截", Toast.LENGTH_SHORT).show();
            }
        });
        btn_share_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScreenShotUtils(ViewWaveActivity.this);
            }
        });
        btn_show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_tools.getVisibility() == View.VISIBLE) {
                    ll_tools.setVisibility(View.GONE);
                    btn_show_button.setText("显示工具条");
                } else {
                    ll_tools.setVisibility(View.VISIBLE);
                    btn_show_button.setText("隐藏工具条");
                }
            }
        });
        btn_upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFiltered=WaveUtils.waveDataEdited(dataFiltered);
                String dataFilteredgson= MyJson.floatArray2Json(dataFiltered);
                /**获取用户信息，上传数据 **/
                String usergson=preferenceUtils.getPreferenceString("user");
                UserInfo userInfo=MyJson.json2User(usergson);
                FileInfo fileInfo=new FileInfo(userInfo.getUserid(),time,dataFilteredgson);
                String fileinfogson=MyJson.file2Json(fileInfo);
                HttpParams params = new HttpParams();
                params.put("newfileinfo",fileinfogson);//文件上传
                RxVolley.post(MyApplication.HOST + "servlet/saddFile", params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        int result=Integer.parseInt(t.trim());
                        if (result>0){
                            SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                            pDialog.setTitleText("波形文件上传结果");
                            pDialog.setContentText("上传成功");
                            pDialog.setConfirmText("确定");
                            pDialog.show();
                        }
                        else {
                            SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                            pDialog.setTitleText("波形文件上传结果");
                            pDialog.setContentText("上传失败");
                            pDialog.setConfirmText("确定");
                            pDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                        pDialog.setTitleText("波形文件上传结果");
                        pDialog.setContentText("上传失败 "+errorNo+":"+strMsg);
                        pDialog.setConfirmText("确定");
                        pDialog.show();
                    }
                });
            }
        });
        btn_look_single_wave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFiltered=WaveUtils.waveDataEdited(dataFiltered);
                GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(ViewWaveActivity.this);
                rPeak=getWaveDataFromFile.getDataRPeak(dataFiltered);
                /**提取单波数据**/
                String usergson=preferenceUtils.getPreferenceString("user");
                UserInfo userInfo=MyJson.json2User(usergson);
                String yangbenwavelisgson=WaveUtils.getSingleWaveListGsonString(dataFiltered,rPeak,userInfo.getUserid(),time);
                //将yangbenwavelisgson发送到单波列表
                Intent intent=new Intent(ViewWaveActivity.this,HomeActivity.class);
                intent.putExtra("id",1);//标记符，若为1则列表启动单波fragment
                preferenceUtils.setPreferenceString("yangbenwavelistgson",yangbenwavelisgson);
                startActivity(intent);
                finish();
            }
        });
        btn_recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Recognize();*/
                RecognizeRaw();

            }
        });
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        // no description text
        mChart.setDescription("");

        // enable touch gestures
        mChart.setTouchEnabled(true);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);

        // set the marker to the chart
        mChart.setMarkerView(mv);
        /**获取intent传来的json波形信息并解析**/
        Intent intent=getIntent();
        String wavedatagson= intent.getStringExtra("wavedatagson");
        time=intent.getLongExtra("wavedatatime",0);
        jiance=intent.getBooleanExtra("jiance",false);
        if (jiance){
            btn_look_single_wave.setVisibility(View.GONE);
            btn_upload_file.setVisibility(View.GONE);
            btn_share_screen.setVisibility(View.GONE);
        }
        dataFiltered=GsonUtils.json2FloatArray(wavedatagson);
        float orginWaveDataMax=WaveUtils.getWaveDataMax(dataFiltered);
        float orginWaveDataMin=WaveUtils.getWaveDataMin(dataFiltered);
        if ((orginWaveDataMax-orginWaveDataMin)<200)
           dataFiltered = WaveUtils.waveDataEdited(dataFiltered);
        GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(this);
        rPeak=getWaveDataFromFile.getDataRPeak(dataFiltered);
        /**获取intent传来的json波形信息并解析**/
        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();


        mChart.getAxisRight().setEnabled(false);


        // add data
        setData(dataFiltered);


        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);

        // // dont forget to refresh the drawing
        // mChart.invalidate();


    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    set.setDrawValues(!set.isDrawValuesEnabled());
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionToggleFilled: {

                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawFilledEnabled())
                        set.setDrawFilled(false);
                    else
                        set.setDrawFilled(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCircles: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCirclesEnabled())
                        set.setDrawCircles(false);
                    else
                        set.setDrawCircles(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleCubic: {
                List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawCubicEnabled())
                        set.setDrawCubic(false);
                    else
                        set.setDrawCubic(true);
                }
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleStepped: {
                /*List<ILineDataSet> sets = mChart.getData()
                        .getDataSets();

                for (ILineDataSet iSet : sets) {

                    LineDataSet set = (LineDataSet) iSet;
                    if (set.isDrawSteppedEnabled())
                        set.setDrawStepped(false);
                    else
                        set.setDrawStepped(true);
                }
                mChart.invalidate();*/
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000, Easing.EasingOption.EaseInCubic);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }
            case R.id.actionSave: {
                if (mChart.saveToPath("title" + System.currentTimeMillis(), "")) {
                    Toast.makeText(getApplicationContext(), "Saving SUCCESSFUL!",
                            Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Saving FAILED!", Toast.LENGTH_SHORT)
                            .show();

                // mChart.saveToGallery("title"+System.currentTimeMillis())
                break;
            }
        }
        return true;
    }

    public void setData(float[] dataFiltered) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < dataFiltered.length; i++) {
            xVals.add((i) + "");
        }

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
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
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
        Drawable drawable1 = ContextCompat.getDrawable(this, R.drawable.fade_red);
        set2.setFillDrawable(drawable1);
        set2.setDrawValues(!set1.isDrawValuesEnabled());
        set2.setDrawFilled(false);
        set2.setDrawCircles(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets
        dataSets.add(set2);
        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        waveDataSetIndex = e.getXIndex();
        Log.i("Entry selected", e.toString());
        KLog.v("data:" + dataFiltered[e.getXIndex()] + "  " + e.getVal());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
    public void Recognize(){
        GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(ViewWaveActivity.this);
        float tempRPeak[]=getWaveDataFromFile.getDataRPeak(dataFiltered);
        if (tempRPeak.length>5){
            String dataFilteredgson= MyJson.floatArray2Json(dataFiltered);
            HttpParams params = new HttpParams();
            params.put("data",dataFilteredgson);//文件上传
            RxVolley.post(MyApplication.HOST + "servlet/sRecognize", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    UserInfo userInfo=MyJson.json2User(t);
                    if (userInfo.getUserid()!=-1){
                        SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                        pDialog.setTitleText("识别结果");
                        pDialog.setContentText(userInfo.getName());
                        pDialog.setConfirmText("确定");
                        pDialog.show();
                    }
                    else {
                        SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                        pDialog.setTitleText("识别结果");
                        pDialog.setContentText("未识别到该用户，可能是没有该用户未注册，请选择重新采集数据或者注册新用户！");
                        pDialog.setConfirmText("重新采集");
                        pDialog.setCancelText("注册用户");
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent(ViewWaveActivity.this, CollectDataActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        pDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent(ViewWaveActivity.this, CollectDataActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        pDialog.show();
                    }
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                    pDialog.setTitleText("识别结果");
                    pDialog.setContentText("识别失败 "+errorNo+":"+strMsg);
                    pDialog.setConfirmText("确定");
                    pDialog.show();
                }
            });
        }else {
            SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
            pDialog.setTitleText("波形中有效波形数量太少");
            pDialog.setContentText("请修改截取范围，是的波形中可识别出的波形数＞5");
            pDialog.setConfirmText("确定");
            pDialog.show();
        }
    }
    public void RecognizeRaw() {
        String dataFilteredgson= MyJson.floatArray2Json(dataFiltered);
        HttpParams params = new HttpParams();
        params.put("data",dataFilteredgson);//文件上传
        RxVolley.post(MyApplication.HOST + "servlet/sRecognizeRaw", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                KLog.v(MyApplication.TAG,"结果："+t);
                UserInfo userInfo=MyJson.json2User(t);
                SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                pDialog.setTitleText("识别结果");
                pDialog.setContentText(userInfo.getName());
                pDialog.setConfirmText("确定");
                pDialog.show();

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                SweetAlertDialog pDialog = new SweetAlertDialog(ViewWaveActivity.this);
                pDialog.setTitleText("识别结果");
                pDialog.setContentText("识别失败 " + errorNo + ":" + strMsg);
                pDialog.setConfirmText("确定");
                pDialog.show();
            }
        });
    }
}
