package cn.edu.tjut.ecggraduationproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.edu.tjut.ecggraduationproject.application.MyApplication;

/**
 * Created by Administrator on 2016/4/10 0010.
 */
public class PreferenceUtils {
    private Context context;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;
    public PreferenceUtils() {
        context= MyApplication.getContextObject();
        mPref=context.getSharedPreferences("config",Context.MODE_PRIVATE);
    }
    public String getPreferenceString(String key){
        return mPref.getString(key,"");
    }
    public void setPreferenceString(String key,String value){
        editor=mPref.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public int getPreferenceInt(String key,int defaultnum){
        return mPref.getInt(key,defaultnum);
    }
    public void setPreferenceInt(String key,int value){
        editor=mPref.edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public boolean getPreferenceBoolean(String key,boolean value){
        return mPref.getBoolean(key,value);
    }
    public void setPreferenceBoolean(String key,boolean value){
        editor=mPref.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
}
