package com.seoul.ddroad.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seoul.ddroad.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PolylineDialog extends DialogFragment {

    public PolylineDialog() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_polyline, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(800, 500);
    }

    @OnClick(R.id.btn_ok)
    public void clickOk() {
        Bundle args = new Bundle();
        args.putParcelableArrayList("pointList", getArguments().getParcelableArrayList("pointList"));
        Intent it = new Intent()
                .putExtras(args);
        getTargetFragment().onActivityResult(1, Activity.RESULT_OK, it);
        dismiss();
    }


    @OnClick(R.id.btn_no)
    public void clickNo() {
        this.dismiss();
    }
}
