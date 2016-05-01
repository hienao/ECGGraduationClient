package cn.edu.tjut.ecggraduationproject.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.lantouzi.wheelview.WheelView;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private PreferenceUtils preferenceUtils;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WheelView mWheelView;
    private TextView mTextView;
    private int databytes;
    private boolean debugMode;
    private Switch switch_debug;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        preferenceUtils=new PreferenceUtils();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting, container, false);
        mWheelView = (WheelView)view.findViewById(R.id.wheelview);
        mTextView = (TextView) view.findViewById(R.id.tv_num_selected);
        switch_debug = (Switch) view.findViewById(R.id.switch_debug);
        List<String> items = new ArrayList<>();
        KLog.v(MyApplication.TAG,"111111111111111");
        items.add("00000");
        items.add("10000");
        items.add("20000");
        items.add("30000");
        items.add("40000");
        items.add("50000");
        items.add("60000");
        items.add("70000");
        items.add("80000");
        items.add("90000");
        mWheelView.setItems(items);
        mWheelView.setMaxSelectableIndex(7);
        mWheelView.setMinSelectableIndex(3);
        databytes=preferenceUtils.getPreferenceInt("dataBytes",50000);
        debugMode=preferenceUtils.getPreferenceBoolean("debugmode",false);
        mTextView.setText("当前选择："+databytes);
        mWheelView.selectIndex(databytes/10000);
        mWheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                preferenceUtils.setPreferenceInt("dataBytes",position*10000);
                int databytes=preferenceUtils.getPreferenceInt("dataBytes",50000);
                mTextView.setText("当前选择："+databytes);
            }

            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                preferenceUtils.setPreferenceInt("dataBytes",position*10000);
                int databytes=preferenceUtils.getPreferenceInt("dataBytes",50000);
                mTextView.setText("当前选择："+databytes);
            }
        });
        if (debugMode==true){
            switch_debug.setText("已开启");
            switch_debug.setChecked(true);
        }else {
            switch_debug.setText("已关闭");
            switch_debug.setChecked(false);
        }
        switch_debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    switch_debug.setText("已开启");
                    preferenceUtils.setPreferenceBoolean("debugmode",true);
                }else {
                    switch_debug.setText("已关闭");
                    preferenceUtils.setPreferenceBoolean("debugmode",false);
                }
            }
        });
        return view;
    }

}
