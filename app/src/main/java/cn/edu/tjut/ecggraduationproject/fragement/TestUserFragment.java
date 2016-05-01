package cn.edu.tjut.ecggraduationproject.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TestUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestUserFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText et_userid;
    private EditText et_username;
    private Button btn_modified;
    private PreferenceUtils preferenceUtils;

    public TestUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestUserFragment newInstance(String param1, String param2) {
        TestUserFragment fragment = new TestUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = new PreferenceUtils();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_user, container, false);
        et_userid = (EditText) view.findViewById(R.id.et_test_userid);
        et_username = (EditText) view.findViewById(R.id.et_test_username);
        btn_modified = (Button) view.findViewById(R.id.btn_test_modified);
        btn_modified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userInfo=new UserInfo(Long.parseLong(et_userid.getText().toString()),0,0,et_username.getText().toString(),"","","");
                HttpParams mParams = new HttpParams();
                mParams.put("userid", String.valueOf( userInfo.getUserid()));
                RxVolley.post(MyApplication.HOST+"servlet/sgetUserInfoById", mParams, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        UserInfo newuserinfo= MyJson.json2User(t);
                        if (newuserinfo.getUserid()!=-1)
                            preferenceUtils.setPreferenceString("user",t);
                        else {
                            SweetAlertDialog  mPDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("获取用户信息失败")
                                    .setContentText("没有对应ID的用户，请修改ID")
                                    .setConfirmText("确定");
                            mPDialog.show();
                        }
                    }
                });
            }
        });
        return view;
    }
}
