package com.seoul.ddroad.diary;



import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seoul.ddroad.MainActivity;
import com.seoul.ddroad.R;

import java.io.File;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {

  //  private TextView comeOn; //상세화면 텍스트뷰
    private TextView diaryDetailDate; //날짜
    private TextView diaryDetailTitle; //title
    private ImageView diaryDetailImage;
    private TextView diaryDetailContent;
    private Button diaryDetailEdit;
    private Button diaryDetailDelete;
    private int diaryId;
    private  String diaryDetailImgDir;

    HashMap<String,Object> diaryMap = new HashMap<String,Object>();
    HashMap<String,Object> diaryImgMap = new HashMap<String,Object>(); //hashmap 선언;

    private SqlLiteDao sqlLiteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //액션바 사용
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("상세내용");
        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        sqlLiteDao = new SqlLiteDao(DetailActivity.this);

        Intent intent = getIntent();
        diaryId = intent.getExtras().getInt("diaryId");


        diaryMap = sqlLiteDao.selectDiary(diaryId); //메서드에서 데이터를 가져옵니다.!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!원래 이거 만들었어야함

        //db에서 이미지 경로 가져오기
        diaryImgMap = sqlLiteDao.selectDiaryImg(diaryId);

       // comeOn = (TextView) findViewById(R.id.comeOn); //상세화면 텍스트 뷰 선언
        diaryDetailDate = (TextView) findViewById(R.id.diaryDetailDate);
        diaryDetailTitle = (TextView) findViewById(R.id.diaryDetailTitle);
        diaryDetailImage = (ImageView) findViewById(R.id.diaryDetailImage);
        diaryDetailContent = (TextView) findViewById(R.id.diaryDetailContent);

        diaryDetailEdit = (Button) findViewById(R.id.diaryDetailEdit);
        diaryDetailDelete = (Button) findViewById(R.id.diaryDetailDelete);


        diaryDetailEdit.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public  void  onClick(View view){
                        Intent intent = new Intent(getApplicationContext(), DiaryRegActivity.class);
                        String redgt = (String)diaryMap.get("regdt");
                        String content = (String)diaryMap.get("content");
                        String title = (String)diaryMap.get("title");
                        String imgstr = (String)diaryMap.get("imgstr");

                        intent.putExtra("diaryId", diaryId);
                        intent.putExtra("imgstr", imgstr);
                        intent.putExtra("redgt", redgt);
                        intent.putExtra("content", content);
                        intent.putExtra("title", title);
                        intent.putExtra("regModCheck", "M");
                        if(diaryImgMap != null) {
                            String imgDir = (String) diaryImgMap.get("imgDir");
                            intent.putExtra("imgDir", imgDir);
                        }else{
                            intent.putExtra("imgDir", "");
                        }
                        startActivity(intent);
                        finish();
                    }
                }
        );

        diaryDetailDelete.setOnClickListener(
                new Button.OnClickListener(){
                    @Override
                    public  void  onClick(View view){

                        new AlertDialog.Builder(DetailActivity.this)
                                .setTitle("다이어리>삭제하기")
                                .setMessage("삭제하시겠습니까?")
                                .setIcon(R.drawable.info_dots)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // 확인시 처리 로직

                                        sqlLiteDao.deleteDiary(diaryId);
                                        Toast.makeText(getApplicationContext(),"삭제 되었습니다.",Toast.LENGTH_LONG).show();
                                        onBackPressed();
                                        finish();

                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // 취소시 처리 로직
                                        Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();

                    }
                }
        );


        //comeOn.setText("권바보야 MAP이 뭐냐~~!!" + diaryMap.toString()); //데이터 확인합니다.
        String redgt = (String)diaryMap.get("regdt");
        String content = (String)diaryMap.get("content");
        String title = (String)diaryMap.get("title");
        diaryDetailDate.setText(redgt);
        diaryDetailContent.setText(content);
        diaryDetailTitle.setText(title);
        String resName = (String)diaryMap.get("imgstr");
        String packName = this.getPackageName(); // 패키지명
        int resID = getResources().getIdentifier(resName, "drawable", packName);
        diaryDetailImage.setImageResource(resID);


        if(diaryImgMap != null) {

            diaryDetailImgDir= (String)diaryImgMap.get("imgDir");

          //  Toast.makeText(getApplicationContext(), diaryDetailImgDir, Toast.LENGTH_LONG).show();
        }
        //이미지뷰뿌리기
        if(diaryDetailImgDir != null && diaryDetailImgDir != "") {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            //  iv.setBackgroundResource(R.drawable.bichon1);
            File f = new File(diaryDetailImgDir);
            Bitmap d = new BitmapDrawable(getResources(), f.getAbsolutePath()).getBitmap();

            d = resizeBitmapImage(d,1024);
            Drawable drawable = new BitmapDrawable(getResources(), d);

            GravityCompoundDrawable gravityDrawable = new GravityCompoundDrawable(drawable);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            gravityDrawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

            //diaryDetailContent.setCompoundDrawablePadding(10);
            diaryDetailContent.setCompoundDrawablesWithIntrinsicBounds(null,drawable,null,null);
            //Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, com.fxn.utility.Utility.getExifCorrectedBitmap(f));
            //Bitmap scaled = com.fxn.utility.Utility.getScaledBitmap(512, d);
            //iv.setImageBitmap(d);
            //diaryDetailImg.addView(iv);
        }


        }

        //비트맵 이미지 리사이즈
    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height)
        {
            if(maxResolution < width)
            {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < height)
            {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("action", "diary");
        startActivity(intent);
        finish();
    }
}


