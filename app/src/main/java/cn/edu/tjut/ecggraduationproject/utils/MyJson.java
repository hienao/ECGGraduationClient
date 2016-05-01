package cn.edu.tjut.ecggraduationproject.utils;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.edu.tjut.ecggraduationproject.common.RPeakDetection;
import cn.edu.tjut.ecggraduationproject.model.FileInfo;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.model.WaveInfo;
import cn.edu.tjut.ecggraduationproject.model.YangbenWaveInfo;


public class MyJson {

	static Gson gson=new Gson();
	public MyJson() {
		// TODO Auto-generated constructor stub
	}
	public static String floatArray2Json(float wave[]) {//120个数字的波形数组转化json
		String jsonstring=gson.toJson(wave);
		return jsonstring;
	}
	public static float[] json2FloatArray(String jsonString) {//json转化120个数字的波形数组
		float wave[]=gson.fromJson(jsonString, float[].class);
		return wave;
	}
	public static String user2Json(UserInfo userInfo) {//将用户信息转换为json字符串
		String jsonstring=gson.toJson(userInfo);
		return jsonstring;
	}
	public static UserInfo json2User(String jsonstring) {//将json转化为用户信息
		UserInfo userInfo=gson.fromJson(jsonstring, UserInfo.class);
		return userInfo;
	}
	public static String file2Json(FileInfo fileInfo) {//将波形信息转换为json字符串
		String jsonstring=gson.toJson(fileInfo);
		return jsonstring;
	}
	public static FileInfo json2File(String jsonstring) {//将json转化为波形信息
		FileInfo fileInfo=gson.fromJson(jsonstring, FileInfo.class);
		return fileInfo;
	}
	public static String yangbenWave2Json(YangbenWaveInfo yangbenWaveInfo) {//将样本波形信息转换为json字符串
		String jsonstring=gson.toJson(yangbenWaveInfo);
		return jsonstring;
	}
	public static YangbenWaveInfo json2YangbenWave(String jsonstring) {//将json转化为样本波形信息
		YangbenWaveInfo yangbenWaveInfo=gson.fromJson(jsonstring, YangbenWaveInfo.class);
		return yangbenWaveInfo;
	}
	public static List<UserInfo> json2UserInfoList(String jsonString) {//将用户信息列表转换为json字符串
		List<UserInfo>listnewInfos=gson.fromJson(jsonString, new TypeToken<List<UserInfo>>() {  
        }.getType());
		return listnewInfos;
	}
	public static String userInfoList2Json(List<UserInfo> list) {//将json转化为用户信息列表
		String jsonstring=gson.toJson(list);
		return jsonstring;
	}
	public static List<FileInfo> json2FileInfoList(String jsonString) {//将波形信息列表转换为json字符串
		List<FileInfo>listnewInfos=gson.fromJson(jsonString, new TypeToken<List<FileInfo>>() {  
        }.getType());
		return listnewInfos;
	}
	public static String fileInfoList2Json(List<FileInfo> list) {//将json转化为波形信息列表
		String jsonstring=gson.toJson(list);
		return jsonstring;
	}
	public static List<YangbenWaveInfo> json2YangbenWaveInfoList(String jsonString) {//将样本波形信息列表转换为json字符串
		List<YangbenWaveInfo>listnewInfos=gson.fromJson(jsonString, new TypeToken<List<YangbenWaveInfo>>() {  
        }.getType());
		return listnewInfos;
	}
	public static String yangbenwaveInfoList2Json(List<YangbenWaveInfo> list) {//将json转化为样本波形信息列表
		String jsonstring=gson.toJson(list);
		return jsonstring;
	}
	public static List<WaveInfo> json2WaveInfoList(String jsonfile) {
		//识别R点，分割波形
		List<WaveInfo> waveinfolist = null;
		float[] rPeak = null;
		float[]data=json2FloatArray(jsonfile);
		RPeakDetection mRPeakDetection = new RPeakDetection();
		rPeak = mRPeakDetection.RPeakRecognize(data);//获取R点值
		if (rPeak[0]>=49&&(rPeak[rPeak.length-1]+70)<=data.length) {//所有R点都够一个单波波长
			 
		} else if (rPeak[0]<49) {
			rPeak=WaveUtils.cutWaveData(rPeak, 1, rPeak.length-1);
		} else {
			rPeak=WaveUtils.cutWaveData(rPeak, 0, rPeak.length-2);
		}
		for (int i = 0; i < rPeak.length; i++) {
			float[]tempsinglewave=Arrays.copyOfRange(data,(int)( rPeak[i]-50),(int)( rPeak[i]+70));
			String singlewavejsonString=MyJson.floatArray2Json(tempsinglewave);
			WaveInfo waveInfo=new WaveInfo(singlewavejsonString);
			waveinfolist.add(waveInfo);
		}
		return waveinfolist;
	}
	
}
