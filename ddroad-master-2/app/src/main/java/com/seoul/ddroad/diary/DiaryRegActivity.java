package com.seoul.ddroad.diary;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.seoul.ddroad.MainActivity;
import com.seoul.ddroad.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.sql.DriverManager.println;

/**
 * Created by guitarhyo on 2018-08-15.
 */
public class DiaryRegActivity extends AppCompatActivity{

    private Spinner spinner;
    private String mImgStr="";
    private TextView weatherDate;
    private Date mCurrentDate; //전역 현재날짜 선언
    private Calendar now;
    private String dateStr = "";
    private String timeStr = "";
    private String weatherDateStr = "";
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private FloatingActionButton fab;
    private String imgDirStr = "";
    private SqlLiteDao sqlLiteImgDao;

    private String redgt = "";
    private String content = "";
    private String title = "";
    private String imgstr = "";

    private int diaryId;

    private String regModCheck = "";


    private EditText diaryTitle;
    private EditText diaryContent;
    @Override
    public void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaryreg);

        diaryTitle=(EditText)findViewById(R.id.diaryTitle);
        diaryContent=(EditText)findViewById(R.id.diaryContent);

        //데이터 가져와서 등록 수정 판별
        Intent intent = getIntent();

        regModCheck= intent.getExtras().getString("regModCheck");
        if("M".equals(regModCheck)){
            diaryId = intent.getExtras().getInt("diaryId");
            title = intent.getExtras().getString("title");
            content = intent.getExtras().getString("content");
            redgt = intent.getExtras().getString("redgt");
            imgstr = intent.getExtras().getString("imgstr");

            imgDirStr = intent.getExtras().getString("imgDir");
        }


        //액션바 사용
        ActionBar ab = getSupportActionBar() ;

        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        // 출처: http://ande226.tistory.com/141 [안디스토리]

        now = Calendar.getInstance();
        mCurrentDate = now.getTime();  //현재 날짜를 가져온다

        //디비 초기화
        sqlLiteImgDao = new SqlLiteDao(DiaryRegActivity.this);

        //상태 이미지 초기화
        spinner =(Spinner)findViewById(R.id.weatherSpinner);


        //이미지 관련 뷰 초기화
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(this);


        //카메라 호출 버튼 초기화
        fab = findViewById(R.id.diaryImgFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pix.start(DiaryRegActivity.this, 100, 1);//선택 개수 정할수있음 일단 1개
            }
        });

        //날씨 이미지 스피너
        String[] arr = getResources().getStringArray(R.array.weather_item_array);
        ArrayList<String> list = new ArrayList<String>();
        for (int i=0; i < arr.length ; i++){
            list.add(arr[i]);
        }

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.weather_spinner_item,list);
        spinner.setAdapter(spinnerAdapter);

        //M은 수정하기 할때
        if("M".equals(regModCheck)){
            ab.setTitle("수정하기");
            diaryTitle.setText(title);
            diaryContent.setText(content);

            if(redgt != null && redgt != "" && redgt.length() > 16){
                SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    mCurrentDate = transFormat.parse(redgt);
                }catch (Exception e){
                    Log.e("ddroad",e.getMessage());
                }
            }
            if("@drawable/bichon1".equals(imgstr) ){
                spinner.setSelection(1);
            }else if("@drawable/bichon2".equals(imgstr)){
                spinner.setSelection(2);
            }else if("@drawable/bichon3".equals(imgstr)){
                spinner.setSelection(3);
            }else if("@drawable/bichon4".equals(imgstr)){
                spinner.setSelection(4);
            }else if("@drawable/bichon5".equals(imgstr)){
                spinner.setSelection(5);
            }else if("@drawable/sun".equals(imgstr)){
                spinner.setSelection(6);
            }else if("@drawable/cloudy".equals(imgstr)){
                spinner.setSelection(7);
            }else if("@drawable/cludysun".equals(imgstr)){
                spinner.setSelection(8);
            }else if("@drawable/drop".equals(imgstr)){
                spinner.setSelection(9);
            }else if("@drawable/flash".equals(imgstr)){
                spinner.setSelection(10);
            }else if("@drawable/snowflake".equals(imgstr)){
                spinner.setSelection(11);
            }else if("@drawable/hospital".equals(imgstr)){
                spinner.setSelection(12);
            }else if("@drawable/pills".equals(imgstr)){
                spinner.setSelection(13);
            }else if("@drawable/diary_walk".equals(imgstr)){
                spinner.setSelection(14);
            }

            if(imgDirStr != null && !"".equals(imgDirStr)){
                ArrayList<String> returnValue = new ArrayList<>();
                returnValue.add(imgDirStr);
                myAdapter.addImage(returnValue);
            }

        }else{
            ab.setTitle("등록하기");
        }

        //화면 표출할 날짜 포멧 String
        weatherDateStr = getDateFormat("yyyy-MM-dd HH:mm",mCurrentDate);


        //달력 텍스트뷰 초기화 및  날짜 셋팅
        weatherDate = (TextView)findViewById(R.id.weatherDate);
        weatherDate.setText(weatherDateStr);

        recyclerView.setAdapter(myAdapter);


        Button btnDate  = (Button)findViewById(R.id.regBtnDate);
        btnDate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                new android.app.DatePickerDialog(
                        DiaryRegActivity.this,
                        new android.app.DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                if(timeStr == ""){
                                    timeStr = getDateFormat("HH:mm",mCurrentDate);
                                }

                                dateStr = ""+year;
                                int monthf = month+1;//1적게 들어오기때문에 더해야함
                                if(monthf > 9){
                                    dateStr += "-"+monthf;
                                }else{
                                    dateStr += "-0"+monthf;
                                }

                                if(dayOfMonth > 9){
                                    dateStr += "-"+dayOfMonth;
                                }else{
                                    dateStr += "-0"+dayOfMonth;
                                }

                                weatherDate.setText(dateStr+" "+timeStr);
                                weatherDateStr=dateStr+" "+timeStr;

                                Log.d("Orignal", weatherDateStr);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        Button btnTime  = (Button)findViewById(R.id.regBtnTime);
        btnTime.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

        new android.app.TimePickerDialog(
                DiaryRegActivity.this,
                new android.app.TimePickerDialog.OnTimeSetListener(){
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        if(dateStr == ""){
                            dateStr = getDateFormat("yyyy-MM-dd",mCurrentDate);
                        }
                        if(hour > 9){
                            timeStr = ""+hour;
                        }else{
                            timeStr = "0"+hour;
                        }

                        if(minute > 9){
                            timeStr += ":"+minute;
                        }else{
                            timeStr += ":0"+minute;
                        }
                        weatherDate.setText(dateStr+" "+timeStr);
                        weatherDateStr=dateStr+" "+timeStr;
                        Log.d("Original", weatherDateStr);
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
               true //24여부
        ).show();
            }
        });





        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               // Toast.makeText(DiaryRegActivity.this,"선택된 아이템 : "+spinner.getItemAtPosition(position),Toast.LENGTH_SHORT).show();
               // Toast.makeText(DiaryRegActivity.this,"선택된 아이템 : "+position,Toast.LENGTH_SHORT).show();
                if(position == 1 ){
                    mImgStr = "@drawable/bichon1";
                }else if(position == 2){
                    mImgStr = "@drawable/bichon2";
                }else if(position == 3){
                    mImgStr = "@drawable/bichon3";
                }else if(position == 4){
                    mImgStr = "@drawable/bichon4";
                }else if(position == 5){
                    mImgStr = "@drawable/bichon5";
                }else if(position == 6){
                    mImgStr = "@drawable/sun";
                }else if(position == 7){
                    mImgStr = "@drawable/clouds";
                }else if(position == 8){
                    mImgStr = "@drawable/cludysun";
                }else if(position == 9){
                    mImgStr = "@drawable/drop";
                }else if(position == 10){
                    mImgStr = "@drawable/flash";
                }else if(position == 11){
                    mImgStr = "@drawable/snowflake";
                }else if(position == 12){
                    mImgStr = "@drawable/hospital";
                }else if(position == 13){
                    mImgStr = "@drawable/pills";
                }else if(position == 14){
                    mImgStr = "@drawable/diary_walk";
                }else{
                    mImgStr = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    /**
     * Action Bar에 메뉴를 생성한다.
     * @param menu
     * @return
     */

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu2, menu);
        return true;
    }

    /**
     * 메뉴 아이템을 클릭했을 때 발생되는 이벤트...
     * @param item
     * @return
     */

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.regPost ){//글 등록 누르면?
            if("M".equals(regModCheck)) {

                //confirm 다이얼 로그 start
                new AlertDialog.Builder(this)
                        .setTitle("다이어리>수정하기")
                        .setMessage("수정하시겠습니까?")
                        .setIcon(R.drawable.info_dots)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 확인시 처리 로직
                                Object[] params = { diaryTitle.getText(), diaryContent.getText(),mImgStr,weatherDateStr,diaryId};
                                sqlLiteImgDao.updatDiary(params);
                                String imgDir = imgFIleWrite();
                                if(imgDir != null && !"".equals(imgDir)){

                                    int cnt = sqlLiteImgDao.getCountDiaryImg(diaryId);
                                    if(cnt > 0){
                                        sqlLiteImgDao.updateDiaryImg(diaryId,imgDir);
                                    }else{
                                        sqlLiteImgDao.insertDiaryImg(diaryId,imgDir);
                                    }

                                }

                                Toast.makeText(DiaryRegActivity.this, "수정을 완료했습니다.", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // 취소시 처리 로직
                                Toast.makeText(DiaryRegActivity.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                //confirm 다이얼 로그 end
            }else{
                Object[] params = { diaryTitle.getText(), diaryContent.getText(),mImgStr,weatherDateStr};
                sqlLiteImgDao.insertDiary(params);


                //이미지 들어갈 다이어리 번호 가져와서 파일 만들구 디비에 넣기
                int diaryId  = sqlLiteImgDao.getLastDiaryId();
                String imgDir = imgFIleWrite();
                if(imgDir != null && imgDir != ""){
                    sqlLiteImgDao.insertDiaryImg(diaryId,imgDir);
                }


                Toast.makeText(getApplicationContext(),
                        "등록 되었습니다.", Toast.LENGTH_SHORT)
                        .show();

            /*Intent intent = new Intent(
                    getApplicationContext(), // 현재 화면의 제어권자
                    DiaryActivity.class); // 다음 넘어갈 클래스 지정
            startActivity(intent); // 다음 화면으로 넘어간다*/
                onBackPressed();
                finish();
            }





            return true;
        }
        return super.onOptionsItemSelected(item);

    }



    public String imgFIleWrite(){
        String returnDirStr = "";
        FileOutputStream outStream = null;

        if(imgDirStr != null && imgDirStr != "") {
            try {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "ddroad");
                if (!mediaStorageDir.exists()) { //폴더 있는지 확인하고 없으면 만든다
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("ddroad", "failed to create directory");

                    }
                }

                returnDirStr = String.format(mediaStorageDir.getPath() + "/ddroad%d.png",
                        System.currentTimeMillis());

                File mediaFile;
                Log.d("ddroad", returnDirStr);
                mediaFile = new File(returnDirStr);
                outStream = new FileOutputStream(mediaFile);

                //카메라에서 가져온 경로로 파일 만듬
                File tmpFile = new File(imgDirStr);
                byte[] data = convertFileToByteArray(tmpFile);

                outStream.write(data);
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                returnDirStr = "";
            } finally {
            }
        }

         return returnDirStr;
    }
    //카메라
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);


                    myAdapter.addImage(returnValue);

                    for (String s : returnValue) {
                        imgDirStr = s;
                        Log.e("val", " ->  " + s);

                    }
                }
            }
            break;
        }
    }

    //카메라
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(DiaryRegActivity.this, 100, 1);
                } else {
                    Toast.makeText(DiaryRegActivity.this, "권한 설정을 해야 카메라 기능을 사용할 수있습니다.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public byte[] convertFileToByteArray(File f)
    {
        byte[] byteArray = null;
        try
        {
            InputStream inputStream = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024*8];
            int bytesRead =0;

            while ((bytesRead = inputStream.read(b)) != -1)
            {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return byteArray;
    }


    private String getDateFormat(String format,Date date){//입력 Date를 날짜를  포팻 형태로 String 출력

        if(format == null || format ==""){
            format  = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(date);
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