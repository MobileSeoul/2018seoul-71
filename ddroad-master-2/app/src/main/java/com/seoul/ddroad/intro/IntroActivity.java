package com.seoul.ddroad.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.seoul.ddroad.R;
import com.seoul.ddroad.map.FillData;

import java.io.InputStream;

public class IntroActivity extends Activity implements Animation.AnimationListener {

    ImageView introView;
    FillData fillData;
    Handler handler = new Handler();


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), PermissionActivity.class);
            startActivity(intent);
            finish();
        }
    };

    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introView = findViewById(R.id.intro_textBalloon);
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        cBounceInterpolar interpolar = new cBounceInterpolar(0.4, 10);
        introView.setAnimation(anim);
        anim.setInterpolator(interpolar);

        fillData = new FillData();
        readFile("cafe");
        readFile("salon");
        readFile("trail");
        readFile("hotel");
        readFile("hospital");

    }

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(runnable, 4000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        introView.setAnimation(null);
    }



    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void readFile(String type) {
        try {
            int raw = 0;

            switch (type) {
                case "cafe":
                    raw = R.raw.cafe;
                    break;
                case "hospital":
                    raw = R.raw.hospital;
                    break;
                case "salon":
                    raw = R.raw.salon;
                    break;
                case "trail":
                    raw = R.raw.trail;
                    break;
                case "hotel":
                    raw = R.raw.hotel;
                    break;
            }

            InputStream is = getResources().openRawResource(raw);
            byte[] readStr = new byte[is.available()];
            is.read(readStr);
            is.close();
            String str[] = (new String(readStr)).split("\n");
            fillData.setData(type, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

