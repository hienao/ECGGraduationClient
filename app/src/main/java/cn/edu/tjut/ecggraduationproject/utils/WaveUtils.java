package cn.edu.tjut.ecggraduationproject.utils;


import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.activity.ViewWaveActivity;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.YangbenWaveInfo;

/**
 * Created by Administrator on 2016/3/16 0016.
 */
public  class WaveUtils {
    public static float[]cutWaveData(float[]root,int startindex,int endindex){
        int length=endindex-startindex;
        float[]resultData=new float[length];
        for (int i=0;i<length;i++){
            resultData[i]=root[startindex+i];
        }
        resultData=waveDataEdited(resultData);
        return resultData;
    }
    public static float getWaveDataMax(float[]root){
        float max=root[0];
        for (int i=0;i<root.length;i++){
            if (root[i]>max)
                max=root[i];
        }
        return max;
    }
    public static float getWaveDataMin(float[]root){
        float min=root[0];
        for (int i=0;i<root.length;i++){
            if (root[i]<min)
                min=root[i];
        }
        return min;
    }
    public static float[]waveDataEdited(float[]root){
        float avgdata=0;
        for (int i=0;i<root.length;i++){
            avgdata+=root[i];
        }
        avgdata=avgdata/root.length;
        for (int i=0;i<root.length;i++){
            root[i]-=avgdata;
        }
        return root;
    }
    public static String getSingleWaveListGsonString(float[]root,float[]rPeaks,long userid,long time){/**得到单波列表的GSON字符串**/
        List<YangbenWaveInfo> list=new ArrayList<YangbenWaveInfo>();
        for (float r:rPeaks){
            if ((int)r-49>=0&&(int)r+70<=root.length-1){
                float singlewave[]= Arrays.copyOfRange(root,(int)r-49,(int)r+71);
                String singlewavegson=MyJson.floatArray2Json(singlewave);
                YangbenWaveInfo yangbenWaveInfo=new YangbenWaveInfo(userid,time,singlewavegson);
                list.add(yangbenWaveInfo);
            }
        }
        String listgson=MyJson.yangbenwaveInfoList2Json(list);
        return listgson;
    }
}
