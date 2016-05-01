package cn.edu.tjut.ecggraduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.abslistview.CommonAdapter;

import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.activity.ViewWaveActivity;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.FileInfo;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.GetWaveDataFromFile;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.edu.tjut.ecggraduationproject.utils.TimeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/4/20 0020.
 */
public class LvFileAdapter extends CommonAdapter<FileInfo> {
    private List<FileInfo> mdatas;
    private Context mContext;
    private int mlayoutId;
    private SweetAlertDialog mPDialog;
    private PreferenceUtils mPreferenceUtils;
    private UserInfo mUserInfo;
    public LvFileAdapter(Context context, int layoutId, List<FileInfo> datas, UserInfo userInfo) {
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
                    RxVolley.post(MyApplication.HOST+"servlet/sgetAllFileDataByUserId", mParams1, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            mdatas.clear();
                            mdatas.addAll( MyJson.json2FileInfoList(t));
                            notifyDataSetChanged();
                        }
                    });
                    mPDialog.dismiss();
                    break;

            }
        }
    };
    @Override
    public void convert(ViewHolder holder, final FileInfo fileInfo) {
        final SwipeLayout swipeLayout=holder.getView(R.id.sl_file_item);
        TextView tv_fileid=holder.getView(R.id.tv_fileid);
        TextView tv_filetime=holder.getView(R.id.tv_filetime);
        RelativeLayout rl_file_view=holder.getView(R.id.rl_file_view);
        RelativeLayout rl_file_del=holder.getView(R.id.rl_file_del);
        tv_fileid.setText("ID："+ fileInfo.getFileid());
        tv_filetime.setText(TimeUtils.millisToLifeString(fileInfo.getTime()));/**这里要调用Utils**/
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.getView(R.id.bottom_wrapper));
        swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (swipeLayout.getOpenStatus()== SwipeLayout.Status.Close)
                    swipeLayout.open();
                else
                    swipeLayout.close();
            }
        });
        rl_file_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(mContext);
                Intent intent=new Intent(mContext,ViewWaveActivity.class);
                String datajson=fileInfo.getFilecontents();
                intent.putExtra("wavedatagson",datajson);
                mContext.startActivity(intent);
            }
        });
        rl_file_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("您确定要删除该波形?")
                        .setContentText("删除后不可恢复，请谨慎！")
                        .setCancelText("不删除")
                        .setConfirmText("删除").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                HttpParams mParams = new HttpParams();
                                mParams.put("fileid", String.valueOf(fileInfo.getFileid()));
                                RxVolley.post(MyApplication.HOST+"servlet/sdeleteFile", mParams, new HttpCallback() {
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
}
