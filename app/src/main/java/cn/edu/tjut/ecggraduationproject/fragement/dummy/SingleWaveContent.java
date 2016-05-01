package cn.edu.tjut.ecggraduationproject.fragement.dummy;

import android.content.Context;
import android.content.SharedPreferences;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.YangbenWaveInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SingleWaveContent {

    private Context context;
    private SharedPreferences sharedPreferences;
    public List<SingleWaveItem> ITEMS = new ArrayList<SingleWaveItem>();

    public SingleWaveContent(Context context) {
        this.context = context;
    }




    public  void createItem(){
        sharedPreferences=context.getSharedPreferences("config",context.MODE_PRIVATE);
        String yangbenwavelistgson=sharedPreferences.getString("yangbenwavelistgson","");
        if (yangbenwavelistgson.isEmpty()){
            yangbenwavelistgson=sharedPreferences.getString("yangbenwavelistgson","");
        }
        List<YangbenWaveInfo> list= MyJson.json2YangbenWaveInfoList(yangbenwavelistgson);
        ITEMS.clear();
        for (YangbenWaveInfo yangbenWaveInfo:list){
            SingleWaveItem singleWaveItem=new SingleWaveItem(yangbenWaveInfo.getWaveid(),yangbenWaveInfo.getUserid(),yangbenWaveInfo.getTime(),yangbenWaveInfo.getData());
            ITEMS.add(singleWaveItem);
        }
    }

    public static class SingleWaveItem {
        public final long waveid;
        public final long userid;
        public final long time;
        public final String data;

        public SingleWaveItem(long waveid, long userid, long time, String data) {
            this.waveid = waveid;
            this.userid = userid;
            this.time = time;
            this.data = data;
        }

        public SingleWaveItem(long userid, long time, String data) {
            this.waveid=0;
            this.userid = userid;
            this.time = time;
            this.data = data;
        }
    }
}
