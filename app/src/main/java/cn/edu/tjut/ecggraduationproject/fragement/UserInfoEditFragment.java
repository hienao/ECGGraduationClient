package cn.edu.tjut.ecggraduationproject.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kymjs.core.bitmap.client.BitmapCore;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.application.MyApplication;
import cn.edu.tjut.ecggraduationproject.model.UserInfo;
import cn.edu.tjut.ecggraduationproject.utils.BitmapUtils;
import cn.edu.tjut.ecggraduationproject.utils.MyJson;
import cn.edu.tjut.ecggraduationproject.utils.PreferenceUtils;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link UserInfoEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoEditFragment extends Fragment {
    private ImageView imageViewphoto;//照片显示框
    private TextView tv_userid;
    private PreferenceUtils preferenceUtils;
    private UserInfo userInfo = null;
    private Spinner genderSpinner;
    private EditText et_username;
    private EditText et_age;
    private EditText et_phoneNum;
    private EditText et_Pwd;
    private EditText et_Pwd_repeat;
    private Button takephotobtn, modifybtn;//照相按钮、下一步按钮、放弃按钮
    private final String[] genderStrParams = {"男", "女"};
    private ArrayAdapter<String> genderAdapter;
    private String genderStr;
    private String photopath = ""; //图片本地路径
    private String photourl = "";
    private Uri uri;//图片URi
    HttpCallback callback;

    public UserInfoEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserInfoEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoEditFragment newInstance(String param1, String param2) {
        UserInfoEditFragment fragment = new UserInfoEditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils=new PreferenceUtils();
        String userinfogson = preferenceUtils.getPreferenceString("user");
        userInfo = MyJson.json2User(userinfogson);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info_edit, container, false);
        imageViewphoto = (ImageView) view.findViewById(R.id.imageViewphoto);
        tv_userid = (TextView) view.findViewById(R.id.tv_userid);
        takephotobtn = (Button) view.findViewById(R.id.takephotobtn);
        modifybtn = (Button) view.findViewById(R.id.btn_modify);
        et_username = (EditText) view.findViewById(R.id.name_ed);
        genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        et_age = (EditText) view.findViewById(R.id.age_ed);
        et_phoneNum = (EditText) view.findViewById(R.id.phoneNum_ed);
        et_Pwd = (EditText) view.findViewById(R.id.et_pwd);
        et_Pwd_repeat = (EditText) view.findViewById(R.id.et_pwd_repeat);
        if (userInfo.getPhotopath().isEmpty()) ;
        else {
            photourl=userInfo.getPhotopath();
            new BitmapCore.Builder()
                    .url(photourl)
                    .callback(callback)
                    .view(imageViewphoto)
                    .loadResId(R.mipmap.ic_launcher)
                    .errorResId(R.mipmap.ic_launcher)
                    .doTask();
        }
        tv_userid.setText(String.valueOf(userInfo.getUserid()));
        et_username.setText(userInfo.getName());
        et_age.setText(String.valueOf(userInfo.getAge()));
        et_phoneNum.setText(String.valueOf(userInfo.getPhone()));
        //将可选内容与ArrayAdapter连接起来
        genderAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, genderStrParams);
        //设置下拉列表的风格
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        genderSpinner.setAdapter(genderAdapter);
        //添加事件Spinner事件监听
        genderSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        genderSpinner.setVisibility(View.VISIBLE);
        if (userInfo.getSex().equals("男")) {
            genderSpinner.setSelection(0);
        } else {
            genderSpinner.setSelection(1);
        }
        takephotobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                letCamera();
            }
        });
        modifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_username.getText().toString().isEmpty() || et_age.getText().toString().isEmpty() || et_phoneNum.getText().toString().isEmpty() || et_Pwd.getText().toString().isEmpty() || !et_Pwd.getText().toString().equals(et_Pwd_repeat.getText().toString()) || photourl.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("信息校验失败");
                    builder.setMessage("个人信息填写不完整，请填写完整！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create();
                    builder.show();
                } else {
                    userInfo.setName(et_username.getText().toString());
                    userInfo.setAge(Integer.parseInt(et_age.getText().toString()));
                    userInfo.setPhone(Long.parseLong(et_phoneNum.getText().toString()));
                    userInfo.setPhotopath(photourl);
                    userInfo.setSex(genderStr);
                    userInfo.setPwdString(et_Pwd.getText().toString());
                    String modifieduserinfo = MyJson.user2Json(userInfo);
                    preferenceUtils.setPreferenceString("user", modifieduserinfo);
                    //发送请求，修改网络数据
                    HttpParams mMParams= new HttpParams();
                    mMParams.put("userid",String.valueOf(userInfo.getUserid()));
                    mMParams.put("newuserinfo",modifieduserinfo);
                    RxVolley.post(MyApplication.HOST+"servlet/smodifyUserById",mMParams, new HttpCallback() {
                        @Override
                        public void onSuccess(String t) {
                            int result=Integer.parseInt(t.trim());
                            SweetAlertDialog pDialog = new SweetAlertDialog(getActivity());
                            pDialog.setTitleText("用户修改结果");
                            if (result>0){
                                pDialog.setContentText("修改成功");
                            }else {
                                pDialog.setContentText("修改失败");
                            }
                            pDialog.setConfirmText("确定");
                            pDialog.show();
                        }
                    });
                }
            }
        });
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            genderStr = genderStrParams[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:// 拍照
                if (resultCode == getActivity().RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP"); //剪裁
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("crop", true);
                    //设置宽高比例
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    //设置裁剪图片宽高
                    intent.putExtra("outputX", 400);
                    intent.putExtra("outputY", 400);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    Toast.makeText(getActivity(), "剪裁图片", Toast.LENGTH_SHORT).show();
                    //广播刷新相册
                    Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intentBc.setData(uri);
                    getActivity().sendBroadcast(intentBc);
                    startActivityForResult(intent, 2); //设置裁剪参数显示图片至ImageView
                } else if (resultCode == getActivity().RESULT_CANCELED) {
                }
                break;
            case 2:
                //图片解析成Bitmap对象
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    photopath= BitmapUtils.saveMyBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "拍摄成功,请稍等，正在处理中", Toast.LENGTH_SHORT).show();
                new BitmapCore.Builder()
                        .url(userInfo.getPhotopath())
                        .callback(callback)
                        .view(imageViewphoto)
                        .loadResId(R.mipmap.ic_launcher)
                        .errorResId(R.mipmap.ic_launcher)
                        .doTask();
                //上传图片后显示网络图片
                File file = new File(photopath); //这里的path就是那个地址的全局变量
                HttpParams params = new HttpParams();
                params.put("file", file);//文件上传
                RxVolley.post(MyApplication.HOST + "servlet/UploadShipServlet", params, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        photourl = MyApplication.HOST + "upload/" + t;
                        new BitmapCore.Builder()
                                .url(photourl)
                                .callback(callback)
                                .view(imageViewphoto)
                                .loadResId(R.mipmap.ic_launcher)
                                .errorResId(R.mipmap.ic_launcher)
                                .doTask();
                        Toast.makeText(getActivity(), "处理完成", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }

    protected void letCamera() {
        // TODO Auto-generated method stub
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String strImgPath = Environment.getExternalStorageDirectory()
                .toString() + "/ECG/DICM/";// 存放照片的文件夹
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
        //deleteAllFiles(new File(strImgPath));
        File out = new File(strImgPath);
        if (!out.exists()) {
            out.mkdirs();
        } else {

        }
        out = new File(strImgPath, fileName);

        strImgPath = strImgPath + fileName;// 该照片的绝对路径
        photopath = strImgPath;
        uri = Uri.fromFile(out);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(imageCaptureIntent, 1);
    }
}
