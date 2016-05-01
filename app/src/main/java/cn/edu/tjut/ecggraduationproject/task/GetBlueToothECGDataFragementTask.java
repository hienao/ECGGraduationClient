package cn.edu.tjut.ecggraduationproject.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.activity.ViewWaveActivity;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.common.CommonManage;
import cn.edu.tjut.ecggraduationproject.fragement.CollectDataFragment;
public class GetBlueToothECGDataFragementTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private CommonManage cm;
    //	private String filestr ;
    private File fi = null;
    private NumberProgressBar progressBarHorizontal;

    private boolean getDataFlag = false;
    private InputStream btInput = null;// 蓝牙数据输入流
    private FileOutputStream fos = null;//
    private int dataBytes;//一次蓝牙传输的字节总数,默认为50000

    public GetBlueToothECGDataFragementTask(Context context, NumberProgressBar progressBarHorizontal, int dataBytes) {
        this.context = context;
        this.progressBarHorizontal = progressBarHorizontal;
        this.dataBytes = dataBytes;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... progresses) {
        // TODO Auto-generated method stub
        super.onProgressUpdate(progresses);
        progressBarHorizontal.setProgress(progresses[0]);
    }

    @Override
    protected String doInBackground(String... params) {
        // TODO Auto-generated method stub
        //		filestr = params[0];
        cm = new CommonManage(context);
        String filestr = cm.creatECGFile(fos, fi);
        fi = new File(filestr);
        try {
            if (CollectDataFragment.mApplication.getBtSocketConnectFlag()) {
                btInput = CollectDataFragment.btSocket.getInputStream();
                //btInput.mark(0);
                //				btInput.reset();
                byte[] b = new byte[dataBytes];
                int readBytes = 0;

                while (readBytes < dataBytes) {
                    int read = btInput.read(b, readBytes, dataBytes - readBytes);
                    System.out.println(read);
                    if (read == -1) {
                        break;
                    }
                    readBytes += read;
                    //调用publishProgress公布进度,最后onProgressUpdate方法将被执行
                    publishProgress((int) ((readBytes / (float) dataBytes) * 100));
                    KLog.v(MyApplication.TAG,"进度："+(int) ((readBytes / (float) dataBytes) * 100));
                }
                //关闭输入流接口和蓝牙连接接口
                btInput.close();
                CollectDataFragment.btSocket.close();
                CollectDataFragment.mApplication.setBtSocketConnectFlag(false);

                fos = new FileOutputStream(fi.getAbsolutePath(), true);
                //output file
                fos.write(b, 0, readBytes);
                //flush this stream
                fos.flush();
                //close this stream
                fos.close();

                getDataFlag = true;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }

        return filestr;
    }

    @Override
    protected void onPostExecute(String result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        TextView collectTextView = (TextView) ((Activity) context).findViewById(R.id.tv_collect_state);
        Button btn_connect_device = (Button) ((Activity) context).findViewById(R.id.btn_connect_device);
        Button btn_collect_data = (Button) ((Activity) context).findViewById(R.id.btn_collect_data);

        if (result != null && result.length() > 0) {
            collectTextView.setText("采集完毕！");

            fi = new File(result);
            if (fi.exists()) {
                //打开波形图实例
                Intent intent = new Intent();
                intent.putExtra("filestr", result);
                intent.setClass(context, ViewWaveActivity.class);
                context.startActivity(intent);
                btn_connect_device.setText("点击重新连接");
                collectTextView.setText("蓝牙连接已断开！");
                btn_collect_data.setVisibility(View.GONE);
                //				Activity activity = (Activity) context;
                //				activity.finish();
            } else {
                Toast.makeText(context, "文件已被移除", Toast.LENGTH_LONG).show();
            }
        }
    }
}

