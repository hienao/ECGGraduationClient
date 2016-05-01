package cn.edu.tjut.ecggraduationproject.fragement;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.adapter.LvPhoneFileAdapter;
import cn.edu.tjut.ecggraduationproject.model.LocalFileInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FileFragment extends Fragment {
    private List<LocalFileInfo> mLocalFileInfoList = new ArrayList<LocalFileInfo>();
    private LvPhoneFileAdapter mLvPhoneFileAdapter = null;
    private ListView mListView;

    public FileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FileFragment newInstance(String param1, String param2) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("本地文件管理");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file, container, false);
        mListView = (ListView) view.findViewById(R.id.lv_local_file);
        String path = "sdcard/ECGBlueToothFile/";
        File file = new File(path);
        mLocalFileInfoList.clear();
        File[] array = file.listFiles();
        for (int i = 0; i < array.length; i++) {
            if (array[i].isFile()) {
                String time1 = array[i].getName().substring(array[i].getName().indexOf("_") + 1);
                String time2 = time1.substring(0, time1.lastIndexOf("."));
                LocalFileInfo localFileInfo = new LocalFileInfo(Long.parseLong(time2), array[i].getName().substring(0, array[i].getName().indexOf("_")), array[i].getAbsolutePath());
                mLocalFileInfoList.add(localFileInfo);
            }
        }
        Collections.sort(mLocalFileInfoList, comparator);
        mLvPhoneFileAdapter = new LvPhoneFileAdapter(getActivity(), R.layout.fragment_file_item, mLocalFileInfoList);
        mListView.setAdapter(mLvPhoneFileAdapter);
        return view;
    }

    Comparator<LocalFileInfo> comparator = new Comparator<LocalFileInfo>() {
        /**
         * 文件日期对比排序接口
         **/

        @Override
        public int compare(LocalFileInfo lhs, LocalFileInfo rhs) {
            if (lhs.getTime() != rhs.getTime()) {
                if (lhs.getTime() - rhs.getTime() > 0)
                    return 1;
                else
                    return -1;
            } else {
                return lhs.getPath().compareTo(rhs.getPath());
            }
        }
    };
}
