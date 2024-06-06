package com.example.trans;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class Setting extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用布局填充器加载 frag_trans.xml 布局文件
        View view = inflater.inflate(R.layout.frag_setting, container, false);
        // 返回加载后的视图
        return view;
    }
}
