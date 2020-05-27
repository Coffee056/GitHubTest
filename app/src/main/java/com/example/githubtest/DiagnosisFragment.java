package com.example.githubtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiagnosisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiagnosisFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText et_diagnosis_region;
    private Button upload_pic_btn;

    public DiagnosisFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiagnosisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiagnosisFragment newInstance(String param1, String param2) {
        DiagnosisFragment fragment = new DiagnosisFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diagnosis, container, false);
        et_diagnosis_region = (EditText) view.findViewById(R.id.diagnosis_region);
        upload_pic_btn = (Button) view.findViewById(R.id.upload_pic_btn);
        upload_pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED ){
                   // ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    choosePhoto();
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                choosePhoto();
            }else{
                Toast.makeText(getActivity(),"You denied the permission",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //选择图片功能
    private  void choosePhoto(){
        // 添加图片的主要代码
        Intent intent = new Intent();
        // 设定类型为image
        intent.setType("image/*");
        // 设置action
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //Log.d("AddActivityTest", "MenuClickEvent: choose pics button");
        // 选中相片后返回本Activity
        startActivityForResult(intent, 10);
    }

    //数据回调方法
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Toast.makeText(getActivity(),"success",Toast.LENGTH_SHORT).show();
            // 如果是选择照片
            if (requestCode == 10) {
                String path = handleImageOnKitKat(data);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                upload_pic_btn.setBackground(new BitmapDrawable(getContext().getResources(),bitmap));
                Toast.makeText(getActivity(),path,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //取相册照片功能用的函数-获取文件路径   content开头URI --> 文件绝对路径
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(getActivity(),uri)){
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        } else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri,null);

        } else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //Log.d("AddActivityTest", "handleImageOnKitKat  imagePath="+imagePath);
        return  imagePath;
    }
    public String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


}
