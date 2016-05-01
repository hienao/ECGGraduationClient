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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.adapter.LvFileAdapter;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.FileInfo;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnlineFileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineFileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    List<FileInfo> mFileInfoList = null;
    PreferenceUtils mPreferenceUtils;
    private static final int CODE = 0x011;
    @Bind(R.id.lv_user)
    ListView mLvUser;
    @Bind(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    private HttpParams mParams;
    private LvFileAdapter mLvFileAdapter;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE:
                    //通知界面改变
                    mLvFileAdapter.notifyDataSetChanged();
                    //刷新状态改变了
                    mRefresh.setRefreshing(false);
                    break;

            }
        }
    };
    private UserInfo mUserInfo;

    public OnlineFileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnlineFileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineFileFragment newInstance(String param1, String param2) {
        OnlineFileFragment fragment = new OnlineFileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("波形文件管理");
        mFileInfoList = new ArrayList<FileInfo>();
        mPreferenceUtils = new PreferenceUtils();
        mParams = new HttpParams();
        mUserInfo = MyJson.json2User(mPreferenceUtils.getPreferenceString("user"));
        mParams.put("userid", String.valueOf(mUserInfo.getUserid()));
        getUserData();
    }

    private void getUserData() {
        RxVolley.post(MyApplication.HOST + "servlet/sgetAllFileDataByUserId", mParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                mFileInfoList.clear();
                mFileInfoList.addAll(MyJson.json2FileInfoList(t));
                //注册监听器
                mRefresh.setOnRefreshListener(OnlineFileFragment.this);
                //设置加载的颜色
                mRefresh.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
                mLvFileAdapter = new LvFileAdapter(getActivity(), R.layout.fragment_online_file_item, mFileInfoList, mUserInfo);
                mLvUser.setAdapter(mLvFileAdapter);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_file, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onRefresh() {
        RxVolley.post(MyApplication.HOST+"servlet/sgetAllFileDataByUserId",mParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                mFileInfoList.clear();
                mFileInfoList.addAll(MyJson.json2FileInfoList(t));
            }
        });
        handler.sendEmptyMessageDelayed(CODE, 500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
