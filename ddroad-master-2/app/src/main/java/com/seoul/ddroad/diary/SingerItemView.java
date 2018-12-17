package com.seoul.ddroad.diary;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seoul.ddroad.R;

public class SingerItemView extends LinearLayout {
    TextView textView2;
    ImageView imageView;

    public SingerItemView(Context context) {
        super(context);

        init(context);
    }

    public SingerItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.diary_item, this, true);


        textView2 = (TextView) findViewById(R.id.textView2);
        imageView= (ImageView) findViewById(R.id.imageView);
    }


    public void setTitle(String title){
        textView2.setText(title);
    }

    public void setImage(int resId){
        imageView.setImageResource(resId);
    }
}
