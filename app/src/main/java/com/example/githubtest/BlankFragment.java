package com.example.githubtest;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "碎片生命周期测试";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView text;

    public BlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment newInstance(String param1, String param2) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        //Log.d(TAG, "onAttach: ");;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "onCreate: "+mParam1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        text = (TextView)view.findViewById(R.id.text);
        text.setText(mParam1);
        Log.d(TAG, "onCreateView: "+mParam1);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: "+mParam1);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: "+mParam1);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause: "+mParam1);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop: "+mParam1);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: "+mParam1);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy: "+mParam1);
    }

}
