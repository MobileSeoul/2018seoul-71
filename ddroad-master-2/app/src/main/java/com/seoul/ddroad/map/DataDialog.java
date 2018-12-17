package com.seoul.ddroad.map;

import android.app.DialogFragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.seoul.ddroad.R;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataDialog extends DialogFragment {
    private static String TAG = DataDialog.class.getSimpleName();
    Data data;
    private Boolean bool_addr = false, bool_tel = false, bool_link = false, bool_detail = false;
    private LatLng curLatlng;
    private String address;

    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_addr)
    TextView tv_addr;
    @BindView(R.id.tv_tel)
    TextView tv_tel;
    @BindView(R.id.tv_link)
    TextView tv_link;
    @BindView(R.id.tv_detail)
    TextView tv_detail;
    @BindView(R.id.layout_btn_map)
    RelativeLayout layout_btn_map;
    @BindView(R.id.layout_btn_call)
    RelativeLayout layout_btn_call;
    @BindView(R.id.layout_addr)
    LinearLayout layout_addr;
    @BindView(R.id.layout_tel)
    LinearLayout layout_tel;
    @BindView(R.id.layout_link)
    LinearLayout layout_link;
    @BindView(R.id.layout_detail)
    LinearLayout layout_detail;

    public DataDialog() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_data, container, false);
        ButterKnife.bind(this, view);

        curLatlng = getArguments().getParcelable("curLatlng");
        data = (Data) getArguments().getSerializable("data");
        getCurAddr();
        setInfo();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(1000, 1050);
    }

    private void setInfo() {
        tv_title.setText(data.getTitle());
        if (!data.getAddress().equals("null")) {
            tv_addr.setText(data.getAddress());
            bool_addr = true;
        }
        if (!data.getTel().equals("null")) {
            tv_tel.setText(data.getTel());
            bool_tel = true;
        }
        if (!data.getLink().equals("null")) {
            tv_link.setText(data.getLink());
            bool_link = true;
        }
        if (!data.getDetail().equals("null")) {
            tv_detail.setText(data.getDetail());
            bool_detail = true;
        }

        setButton(bool_addr, bool_tel, bool_link, bool_detail);
    }

    private void setButton(boolean addr, boolean tel, boolean link, boolean detail) {
        if (addr) {
            layout_addr.setVisibility(View.VISIBLE);
            layout_btn_map.setVisibility(View.VISIBLE);
        }
        if (tel) {
            layout_tel.setVisibility(View.VISIBLE);
            layout_btn_call.setVisibility(View.VISIBLE);
        }
        if (link)
            layout_link.setVisibility(View.VISIBLE);
        if (detail)
            layout_detail.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_cancel)
    public void clickX() {
        dismiss();
    }

    @OnClick(R.id.btn_map)
    public void clickMap() {
        String uri = "https://maps.google.com/maps?f=d&saddr=" + address + "&daddr=" + data.getAddress() + "&hl=ko";
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        it.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(it);
    }

    @OnClick(R.id.btn_call)
    public void clickCall() {
        String tel = "tel:" + data.getTel();
        try {
            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCurAddr() {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> resultList = geocoder.getFromLocation(curLatlng.latitude, curLatlng.longitude, 1);
            address = resultList.get(0).getAddressLine(0);
            Log.d(TAG, "onComplete: " + address);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onComplete: 주소변환 실패");
        }
    }
}
