package cn.edu.tjut.ecggraduationproject.fragement;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.adapter.LvYangbenAdapter;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.model.YangbenWaveInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link YangbenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YangbenFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    @Bind(R.id.lv_yangben)
    ListView mLvYangben;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    List<YangbenWaveInfo> mYangbenWaveInfoList=null;
    PreferenceUtils mPreferenceUtils;
    private static final int CODE = 0x011;
    private HttpParams mParams;
    private LvYangbenAdapter mLvYangbenAdapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE:
                    //通知界面改变
                    mLvYangbenAdapter.notifyDataSetChanged();
                    //刷新状态改变了
                    mRefresh.setRefreshing(false);
                    break;

            }
        }
    };
    private UserInfo mUserInfo;
    public YangbenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YangbenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YangbenFragment newInstance(String param1, String param2) {
        YangbenFragment fragment = new YangbenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("样本文件管理");
        mYangbenWaveInfoList=new ArrayList<YangbenWaveInfo>();
        mPreferenceUtils=new PreferenceUtils();
        mParams = new HttpParams();
        mUserInfo = MyJson.json2User(mPreferenceUtils.getPreferenceString("user"));
        mParams.put("userid", String.valueOf(mUserInfo.getUserid()));
        getUserData();
    }
    private void getUserData(){
        RxVolley.post(MyApplication.HOST+"servlet/sgetAllYangbenWaveDataByUserId", mParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                KLog.v(MyApplication.TAG,t);
                mYangbenWaveInfoList.clear();
                mYangbenWaveInfoList.addAll(MyJson.json2YangbenWaveInfoList(t));
                //注册监听器
                mRefresh.setOnRefreshListener(YangbenFragment.this);
                //设置加载的颜色
                mRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
                mLvYangbenAdapter=new LvYangbenAdapter(getActivity(), R.layout.yangben_list_item,mYangbenWaveInfoList,mUserInfo);
                mLvYangben.setAdapter(mLvYangbenAdapter);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_yangben, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        RxVolley.post(MyApplication.HOST+"servlet/sgetAllYangbenWaveDataByUserId",mParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                mYangbenWaveInfoList.clear();
                mYangbenWaveInfoList.addAll(MyJson.json2YangbenWaveInfoList(t));
            }
        });
        handler.sendEmptyMessageDelayed(CODE, 500);
    }
}
