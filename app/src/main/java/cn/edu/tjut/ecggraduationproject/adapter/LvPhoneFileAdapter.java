package cn.edu.tjut.ecggraduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.zhy.base.adapter.ViewHolder;
import com.zhy.base.adapter.abslistview.CommonAdapter;

import java.io.File;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.activity.ViewWaveActivity;
import cn.edu.tjut.ecggraduationproject.model.LocalFileInfo;
import cn.edu.tjut.ecggraduationproject.utils.GetWaveDataFromFile;
import cn.edu.tjut.ecggraduationproject.utils.GsonUtils;
import cn.edu.tjut.ecggraduationproject.utils.TimeUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2016/4/23 0023.
 */
public class LvPhoneFileAdapter extends CommonAdapter<LocalFileInfo> {
    private List<LocalFileInfo> mdatas;
    private Context mContext;
    private int mlayoutId;
    private SweetAlertDialog mPDialog;
    public LvPhoneFileAdapter(Context context, int layoutId, List<LocalFileInfo> datas) {
        super(context, layoutId, datas);
        this.mdatas=datas;
        this.mContext=context;
        this.mlayoutId=layoutId;
    }

    private static final int GETUSERLIST = 0x011;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETUSERLIST:
                    String path="sdcard/ECGBlueToothFile/";
                    File file = new File(path);
                    mdatas.clear();
                    File[] array = file.listFiles();
                    for(int i=0;i<array.length;i++){
                        if(array[i].isFile()){
                            LocalFileInfo localFileInfo=new LocalFileInfo(array[i].lastModified(),array[i].getName().substring(0,array[i].getName().indexOf("_")),array[i].getAbsolutePath());
                            mdatas.add(localFileInfo);
                        }
                    }
                    notifyDataSetChanged();
                    mPDialog.dismiss();
                    break;

            }
        }
    };
    @Override
    public void convert(ViewHolder holder, final LocalFileInfo localFileInfo) {
        final SwipeLayout swipeLayout=holder.getView(R.id.sl_file_item);
        TextView tv_local_file_time=holder.getView(R.id.tv_local_file_time);
        TextView tv_local_file_name=holder.getView(R.id.tv_local_file_name);
        RelativeLayout rl_local_file_view=holder.getView(R.id.rl_local_file_view);
        RelativeLayout rl_local_file_del=holder.getView(R.id.rl_local_file_del);
        tv_local_file_time.setText(TimeUtils.millisToLifeString(localFileInfo.getTime()));
        tv_local_file_name.setText(localFileInfo.getName());
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
        rl_local_file_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetWaveDataFromFile getWaveDataFromFile=new GetWaveDataFromFile(mContext);
                float[]data=getWaveDataFromFile.getDataFilteredFromPath(localFileInfo.getPath());
                long time=getWaveDataFromFile.getTimeFromPath(localFileInfo.getPath());
                String wavegson= GsonUtils.floatArray2Json(data);
                Intent intent=new Intent(mContext,ViewWaveActivity.class);
                intent.putExtra("wavedatagson",wavegson);
                intent.putExtra("wavedatatime",time);
                mContext.startActivity(intent);
            }
        });
        rl_local_file_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPDialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("您确定要删除该波形?")
                        .setContentText("删除后不可恢复，请谨慎！")
                        .setCancelText("不删除")
                        .setConfirmText("删除").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                File file = new File(localFileInfo.getPath());
                                deleteFile(file);
                                handler.sendEmptyMessageDelayed(GETUSERLIST, 100);
                            }
                        });
                mPDialog.show();
            }
        });
    }
    //删除SD卡中的文件
    public void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            }else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
        } else {
            Toast.makeText(mContext, "文件已被删除或移除！", Toast.LENGTH_LONG).show();
        }
    }
}
