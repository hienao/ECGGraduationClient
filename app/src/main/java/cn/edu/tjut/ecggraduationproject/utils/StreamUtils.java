package cn.edu.tjut.ecggraduationproject.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读取流的工具
 * Created by Administrator on 2016/3/1 0001.
 */
public class StreamUtils {
    /*
    * 讲输入流读取成string返回
    * */
    public static String readFromStream(InputStream inputStream) throws IOException {

        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int len=0;
        byte[]buffer=new byte[1024];
        while ((len=inputStream.read(buffer))!=-1){
            out.write(buffer,0,len);
        }
        String result=out.toString();
        inputStream.close();
        out.close();
        return result;
    }
}
