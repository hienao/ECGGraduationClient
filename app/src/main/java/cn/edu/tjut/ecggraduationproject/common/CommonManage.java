package cn.edu.tjut.ecggraduationproject.common;

import android.content.Context;

import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;


public class CommonManage {
    private Context context;
   /* private String beginTime;*/
    private long collectTime;
    private PreferenceUtils preferenceUtils;
    private UserInfo userInfo;
    public CommonManage(Context context) {
        this.context = context;
        preferenceUtils=new PreferenceUtils();
    }


    public String creatECGFile(FileOutputStream fos, File fi) {
        String userinfogson=preferenceUtils.getPreferenceString("user");

        if (!userinfogson.equals("")){
            userInfo= MyJson.json2User(userinfogson);
        }else {
            userInfo=new UserInfo();
            userInfo.setName("检测");
        }
        //创建文件
        File fidirectory = new File("sdcard/ECGBlueToothFile/");
        if (!fidirectory.exists()) {
            fidirectory.mkdir();
        }
       /* Calendar mCalendar = Calendar.getInstance();
        //日期格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        //用时间命名文件名
        String fileId = "" + mCalendar.get(Calendar.YEAR) + (mCalendar.get(Calendar.MONTH) + 1) +
                +mCalendar.get(Calendar.DAY_OF_MONTH) + mCalendar.get(Calendar.HOUR_OF_DAY) +
                +mCalendar.get(Calendar.MINUTE) + mCalendar.get(Calendar.SECOND);
        beginTime = simpleDateFormat.format(mCalendar.getTime()).toString();*/
        collectTime =System.currentTimeMillis();

        String filestr = "sdcard/ECGBlueToothFile/"+userInfo.getName();
        filestr += "_";
        filestr += String.valueOf(collectTime);
        filestr += ".txt";
        fi = new File(filestr);
        if (!fi.exists()) {
            KLog.v(MyApplication.TAG, "创建文件");
            try {
                fi.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                KLog.v(MyApplication.TAG, e.toString());
            }
        }

        //将用户信息数据以及测量时间封装成JSON数据格式写入文件
        String testInfoStr = getJsonStr();
        KLog.v(MyApplication.TAG, "----  " + testInfoStr);
        testInfoStr += "##";
        try {
            byte[] testInfoBytes = testInfoStr.getBytes();
            fos = new FileOutputStream(fi.getAbsolutePath(), true);
            //output file
            fos.write(testInfoBytes);
            //flush this stream
            fos.flush();
            //close this stream
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return filestr;
    }

    private String getJsonStr() {
        JSONObject testInfoJSONObject = new JSONObject();
        try {
            /**若要在文件开头写入身份信息等参考以下被注释内容填写*/
            /*testInfoJSONObject.put("name", CollectDataActivity.mApplication.getName());
            testInfoJSONObject.put("gender", CollectDataActivity.mApplication.getGender());
            testInfoJSONObject.put("age", CollectDataActivity.mApplication.getAge());
            testInfoJSONObject.put("phoneNum", CollectDataActivity.mApplication.getPhoneNum());
            testInfoJSONObject.put("pwd", CollectDataActivity.mApplication.getPwd());
            testInfoJSONObject.put("beginTime", beginTime);*/
            testInfoJSONObject.put("collectTime", String.valueOf(collectTime));
            return testInfoJSONObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}

