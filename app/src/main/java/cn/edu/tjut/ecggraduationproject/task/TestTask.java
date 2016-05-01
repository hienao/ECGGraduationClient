package cn.edu.tjut.ecggraduationproject.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.socks.library.KLog;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;

/**
 * Created by Administrator on 2016/4/12 0012.
 */
public class TestTask extends AsyncTask<String, Integer, String> {
    private Context context;
    private com.daimajia.numberprogressbar.NumberProgressBar progressBarHorizontal;

    public TestTask(Context context, NumberProgressBar progressBarHorizontal) {
        this.context = context;
        this.progressBarHorizontal = progressBarHorizontal;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        KLog.v(MyApplication.TAG,values[0]);
        progressBarHorizontal.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        TextView collectTextView = (TextView) ((Activity) context).findViewById(R.id.tv_collect_state);
        Button btn_connect_device = (Button) ((Activity) context).findViewById(R.id.btn_connect_device);
        Button btn_collect_data = (Button) ((Activity) context).findViewById(R.id.btn_collect_data);
    }

    @Override
    protected String doInBackground(String... params) {
        for (int i=0;i<10000;i++)
            publishProgress(i/100);
        return null;
    }
}
