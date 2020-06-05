package com.inveno.xiandu.view.main.store;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.inveno.xiandu.R;
import com.inveno.xiandu.view.BaseFragment;

/**
 * Created By huzheng
 * Date 2020/6/5
 * Des
 */
public class StoreItemFragment extends BaseFragment {

    String title;
    TextView textView;

    public StoreItemFragment() {
    }

    public void setTitle(String title) {
        this.title = title;
//        textView.setText(title);
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_store_item, container, false);
        textView = view.findViewById(R.id.title);
        textView.setText(title);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
