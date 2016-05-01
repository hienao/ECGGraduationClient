package cn.edu.tjut.ecggraduationproject.fragement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import java.util.ArrayList;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.fragement.dummy.SingleWaveContent.SingleWaveItem;
import cn.edu.tjut.ecggraduationproject.model.YangbenWaveInfo;
import cn.edu.tjut.ecggraduationproject.utils.GetWaveDataFromFile;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.TimeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SingleWaveItem} and makes a call to the

 * TODO: Replace the implementation with code for your data type.
 */
public class MySingleWaveItemRecyclerViewAdapter extends RecyclerView.Adapter<MySingleWaveItemRecyclerViewAdapter.ViewHolder> {

    private final List<SingleWaveItem> mValues;
    private Context mycontext,parentcontext;
    boolean isloadfromlocalfile=false;
    public MySingleWaveItemRecyclerViewAdapter(List<SingleWaveItem> items, Context context,Boolean islocalfile) {
        mValues = items;
        mycontext=context;
        isloadfromlocalfile=islocalfile;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_singlewave_item, parent, false);
        parentcontext=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mDelButton.setVisibility(View.GONE);
        holder.mUploadButton.setVisibility(View.VISIBLE);

        holder.mChart.setDescription("");
        holder.mChart.setDrawGridBackground(false);
        holder.mChart.setTouchEnabled(true);
        // enable scaling and dragging
        holder.mChart.setDragEnabled(true);
        holder.mChart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        holder.mChart.setPinchZoom(true);
       /* MyMarkerView mv = new MyMarkerView(HomeActivity.this, R.layout.custom_marker_view);*/
        XAxis xAxis = holder.mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = holder.mChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(15f);

        YAxis rightAxis = holder.mChart.getAxisRight();
        rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(15f);
        float []data=null;
        data= MyJson.json2FloatArray(holder.mItem.data);
        holder.mChart.setData(setData(data));
        holder.mChart.animateY(700, Easing.EasingOption.EaseInCubic);
        holder.mChart.invalidate();
        holder.mTimeView.setText(TimeUtils.millisToLifeString(mValues.get(position).time));
        holder.mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动后台任务
                YangbenWaveInfo yangbenWaveInfo=new YangbenWaveInfo(holder.mItem.userid,holder.mItem.time,holder.mItem.data);
                String yangbenWaveInfoGson=MyJson.yangbenWave2Json(yangbenWaveInfo);
                HttpParams params = new HttpParams();
                params.put("newyangbeninfo",yangbenWaveInfoGson);//文件上传
                RxVolley.post(MyApplication.HOST + "servlet/saddYangbenWave", params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        int result=Integer.parseInt(t.trim());
                        if (result>0){
                            SweetAlertDialog pDialog = new SweetAlertDialog(parentcontext);
                            pDialog.setTitleText("样本文件上传结果");
                            pDialog.setContentText("上传成功");
                            pDialog.setConfirmText("确定");
                            pDialog.show();
                        }
                        else {
                            SweetAlertDialog pDialog = new SweetAlertDialog(parentcontext);
                            pDialog.setTitleText("样本文件上传结果");
                            pDialog.setContentText("上传失败");
                            pDialog.setConfirmText("确定");
                            pDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        SweetAlertDialog pDialog = new SweetAlertDialog(parentcontext);
                        pDialog.setTitleText("样本文件上传结果");
                        pDialog.setContentText("上传失败 "+errorNo+":"+strMsg);
                        pDialog.setConfirmText("确定");
                        pDialog.show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTimeView;
        public final LineChart mChart;
        public final Button mDelButton,mUploadButton;
        public SingleWaveItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mChart= (LineChart) view.findViewById(R.id.chart_singlewave);
            mTimeView = (TextView) view.findViewById(R.id.tv_singlewave_time);
            mDelButton = (Button) view.findViewById(R.id.btn_singlewave_del);
            mUploadButton = (Button) view.findViewById(R.id.btn_singlewave_upload);
        }
    }
    public LineData setData(float[] dataFiltered) {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < dataFiltered.length; i++) {
            xVals.add((i) + "");
        }
        float[]rPeak=null;
        GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(parentcontext);
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
        Drawable drawable = ContextCompat.getDrawable(parentcontext, R.drawable.fade_red);
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
        Drawable drawable1 = ContextCompat.getDrawable(parentcontext, R.drawable.fade_red);
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
