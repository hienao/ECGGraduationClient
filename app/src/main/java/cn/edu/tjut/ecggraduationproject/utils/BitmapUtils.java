package cn.edu.tjut.ecggraduationproject.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/3/23 0023.
 */
public class BitmapUtils {
    public static String saveMyBitmap(Bitmap bitmap) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory()
                .toString() + "/ECG/DICM/");
        if (!file.exists())
            file.mkdir();
        long  time=System.currentTimeMillis();
        String photopath=Environment.getExternalStorageDirectory()
                .toString() + "/ECG/DICM/"+time+".jpg";
        File f = new File(photopath);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photopath;
    }
}
