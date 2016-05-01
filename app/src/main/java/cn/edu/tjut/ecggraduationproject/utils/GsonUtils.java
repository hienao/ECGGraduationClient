package cn.edu.tjut.ecggraduationproject.utils;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/3/16 0016.
 */
public class GsonUtils {
    static Gson gson=new Gson();

    public GsonUtils() {
    }
    public static String floatArray2Json(float wave[]) {//波形数组转化json
        String jsonstring=gson.toJson(wave);
        return jsonstring;
    }
    public static float[] json2FloatArray(String jsonString) {//json转化波形数组
        float wave[]=gson.fromJson(jsonString, float[].class);
        return wave;
    }
}
