package cn.edu.tjut.ecggraduationproject.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.view.Display;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.edu.tjut.ecggraduationproject.application.MyApplication;


/**
 * Created by Administrator on 2016/4/10 0010.
 */
public class ScreenShotUtils {
    public ScreenShotUtils(Activity activity) {
        Bitmap bitmap=myShot(activity);
        try {
            saveToSD(bitmap,Environment.getExternalStorageDirectory()
                    .toString() + "/ECG/DICM/","screen.png");
            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("image/*");
            File file=new File(Environment.getExternalStorageDirectory()
                    .toString() + "/ECG/DICM/screen.png");
            Uri uri = Uri.fromFile(file);

            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            activity.startActivity(Intent.createChooser(shareIntent, "请选择"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap myShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }
    private void saveToSD(Bitmap bmp, String dirName,String fileName) throws IOException {
        // 判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(dirName);
            // 判断文件夹是否存在，不存在则创建
            if(!dir.exists()){
                dir.mkdir();
            }

            File file = new File(dirName + fileName);
            // 判断文件是否存在，不存在则创建
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    // 第一参数是图片格式，第二个是图片质量，第三个是输出流
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    // 用完关闭
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
