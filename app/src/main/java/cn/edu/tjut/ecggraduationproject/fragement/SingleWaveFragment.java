package cn.edu.tjut.ecggraduationproject.fragement;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.edu.tjut.ecggraduationproject.R;
import cn.edu.tjut.ecggraduationproject.fragement.dummy.SingleWaveContent;
/**
 * A fragment representing a list of Items.
 * <p/>
 * interface.
 */
public class SingleWaveFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    /*private OnListFragmentInteractionListener mListener;*/
    String singlewavelistgson;
    private SharedPreferences mPref;
    boolean islocalfile=false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SingleWaveFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SingleWaveFragment newInstance(int columnCount) {
        SingleWaveFragment fragment = new SingleWaveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
        mPref =getActivity().getSharedPreferences("config",0x0000);
        singlewavelistgson=mPref.getString("yangbenwavelisgson","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_singlewave_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            SingleWaveContent singleWaveContent=new SingleWaveContent(getActivity());
            singleWaveContent.createItem();
            recyclerView.setAdapter(new MySingleWaveItemRecyclerViewAdapter(singleWaveContent.ITEMS,getActivity(),islocalfile));
        }
        return view;
    }


    public void getsinglewavedata(String s){
        this.singlewavelistgson=s;
    }
    public void isloadfromlocalfile(boolean b){
        this.islocalfile=b;
    }
}
