package com.seoul.ddroad.diary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import com.seoul.ddroad.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.seoul.ddroad.FontsOverride.setDefaultFont;
import static java.sql.DriverManager.println;


/**
 * Created by guitarhyo on 2018-08-15.
 */
public class DiaryActivity extends AppCompatActivity {

    private CaldroidFragment caldroidFragment;//달력 선언
    private Date mCurrentDate; //전역 현재날짜 선언
    private ListView listView; //다이어리 리스트뷰

    private String diaryTableName = "diary"; //테이블 이름
    private String diaryDatabaseName = "ddroad.db"; //데이터베이스 이름
    SqlLiteOpenHelper helper;
    SQLiteDatabase database;  // database를 다루기 위한 SQLiteDatabase 객체 생성

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        listView = (ListView) findViewById(R.id.listView); //다이어리 리스트 뷰 선언

        //액션바 사용
        ActionBar ab = getSupportActionBar() ;
        ab.setTitle("캘린더") ;
        //메뉴바에 '<' 버튼이 생긴다.(두개는 항상 같이다닌다)
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        // 출처: http://ande226.tistory.com/141 [안디스토리]


        initDBTable();//액티비티 생성시 디비를 생성

        caldroidFragment = new CalendarFragment(); //달력 생성자 생성

//왜 전역 변수와 지역 변수를 쓰는지 알아볼것~
        mCurrentDate = Calendar.getInstance().getTime();  //현재 날짜를 가져온다
        Date currentDate = Calendar.getInstance().getTime(); //지역 변수로 현재 날짜를 가져온다. 이 변수는 디비에 넣을때 필요해서?

//Date 타입 변수를 알아보고 시작하세요~
//selectDiaryListView() 함수 먼저 완성 하고 시작하세요  ---> selectDiaryListView(Date타입 파라미터); 로 호출 하면 현재 날짜로 리스트 뿌리겠지?
//여기에 현재 날짜 받아서 최초 한번 리스트 한번 출력 해야함~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~start
        selectDiaryListView(mCurrentDate);
//여기에 현재 날짜 받아서 최초 리스트 한번 출력 해야함~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~end


        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            caldroidFragment.setArguments(args);
        }

        caldroidFragment.setSelectedDate(currentDate);

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        //달력 이벤트 관련 리스너
        final CaldroidListener listener = new CaldroidListener() {
            @Override //달력 일 클릭 시
            public void onSelectDate(Date date, View view) {
                caldroidFragment.clearSelectedDates();
                caldroidFragment.setSelectedDate(date);
                caldroidFragment.refreshView();
                mCurrentDate = date;

//여기에 선택한 날짜로 리스트뷰 클리어 후 출력 해야함~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~start
                selectDiaryListView(date);
//여기에 선택한 날짜로 리스트뷰 클리어 후 출력 해야함~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~start
            }

            @Override //달력 년도월 변경될때
            public void onChangeMonth(int month, int year) {

            }

            @Override //달력 일 길게 클릭 시
            public void onLongClickDate(Date date, View view) {
               //test용임
                /* Toast.makeText(getApplicationContext(),
                        "길게", Toast.LENGTH_SHORT)
                        .show();
                database = helper.getWritableDatabase();
                if(database != null){
                    Random randomGenerator = new Random();
                    int randomInteger = randomGenerator.nextInt(100); //0 ~ 99 사이의 int를 랜덤으로 생성
                    String title = "title"+randomInteger;
                    String content = "content"+randomInteger;
                    String imgstr = "@drawable/dog1";
                    String sql = "insert into diary(title, content,imgstr ,regdt) values(?, ?,?,datetime('now','localtime'))";
                    Object[] params = { title, content,imgstr};
                    database.execSQL(sql, params);
                }
                */
            }

            @Override //달력 그리기 완성 되고
            public void onCaldroidViewCreated() {
                if (caldroidFragment.getLeftArrowButton() != null) {
                   //달력만들기 성공이면
                }
            }
        };
        // Setup Caldroid
        caldroidFragment.setCaldroidListener(listener);

    }


    //달력에 색깔 입히기
    private void setCustomResourceForDates() {
        Calendar cal = Calendar.getInstance();

        // Min date is last 7 days
        cal.add(Calendar.DATE, -7);
        Date blueDate = cal.getTime();

        // Max date is next 7 days
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        Date greenDate = cal.getTime();

        if (caldroidFragment != null) {
            ColorDrawable blue = new ColorDrawable(getResources().getColor(R.color.blue));
            ColorDrawable green = new ColorDrawable(Color.GREEN);
            caldroidFragment.setBackgroundDrawableForDate(blue, blueDate);
            caldroidFragment.setBackgroundDrawableForDate(green, greenDate);
            caldroidFragment.setTextColorForDate(R.color.white, blueDate);
            caldroidFragment.setTextColorForDate(R.color.white, greenDate);
        }
    }

    class SingerAdapter extends BaseAdapter {
        ArrayList<SingerItem> items = new ArrayList<SingerItem>();


        @Override
        public int getCount() {
            return items.size();    //리스트 뷰가 몇개 있는지
        }

        public void addItem(SingerItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int position) {  //몇번째 데이터를 달라
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {  //Id값 넘겨달라
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingerItemView view = new SingerItemView(getApplicationContext());

            SingerItem item = items.get(position);
            view.setTitle(item.getTitle());
            view.setImage(item.getResId());
            return view;
        }
    }
    private void selectDiaryListView(Date date) {//DB 조회 후 리스트뷰에 담기

        List<HashMap<String,Object>> diaryList = selectDiaryData(date); //DB 조회 후 해시 맵 리스트에 담는다

        //데이터 확인 test용임
      //  for(int i=0; i< diaryList.size();i++){
      //      TextView textView = findViewById(R.id.textView3);
      //      textView.setText((String)diaryList.get(i).get("regdt"));
      //  }

        SingerAdapter adapter = new SingerAdapter();

        for(int i=0; i< diaryList.size();i++){
            Map<String, Object> map = diaryList.get(i);
            String title = (String)map.get("title");
            String imgstr = (String)map.get("imgstr");
            int diaryId = (int)map.get("diaryId");
            String resName = imgstr;
            String packName = this.getPackageName(); // 패키지명
            int resID = getResources().getIdentifier(resName, "drawable", packName);
            //출처: http://kheru.tistory.com/54
            adapter.addItem(new SingerItem( title, diaryId,  resID));
        }

        listView.setAdapter(adapter);

        //리스트뷰의 아이템을 클릭시 해당 아이템의 문자열을 가져오기 위한 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long id) {


                //클릭한 아이템을 가져옵니다.
                SingerItem item = (SingerItem) adapterView.getItemAtPosition(position) ;

                int diaryId = item.getListId();
//////////////////////////////////
 //여기가 리스트를 클릭해서 다이어리 아이디를 가져옵니다.
 ////상세 액티비티에서 DB로 조회 후 데이터를 가져와 뿌려주면 될듯? 레이아웃도 만들어야겠지?

                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("diaryId", diaryId);
                startActivity(intent);

            }
        });

    }



    /**
     * Action Bar에 메뉴를 생성한다.
     * @param menu
     * @return
     */

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    /**
     * 메뉴 아이템을 클릭했을 때 발생되는 이벤트...
     * @param item
     * @return
     */

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if( id == R.id.newPost ){

           // Toast.makeText(DiaryActivity.this, "새 글 등록 버튼을 클릭했습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(
                    getApplicationContext(), // 현재 화면의 제어권자
                    DiaryRegActivity.class); // 다음 넘어갈 클래스 지정
            startActivity(intent); // 다음 화면으로 넘어간다
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void initDBTable(){
        // 액티비티가 켜지면 데이터베이스 생성
        helper = new SqlLiteOpenHelper(this, // 현재 화면의 context
                diaryDatabaseName, // 파일명
                null, // 커서 팩토리
                1); // 버전 번호
       // database = openOrCreateDatabase(diaryDatabaseName,MODE_PRIVATE,null);  //없으면 생성하고 있으면 그대로
        database = helper.getWritableDatabase();
        if(database != null){

            String sql = "CREATE  TABLE IF NOT EXISTS " + diaryTableName + "(diaryId integer PRIMARY KEY autoincrement, title text, content text,imgstr text,regdt text)"; //테이블 확인후 생성
            database.execSQL(sql);

        }else{
            Log.d("ddroad","먼저 데이터베이스를 오픈하세요.");
        }
    }

    public List<HashMap<String,Object>> selectDiaryData(Date date){   // 항상 DB문을 쓸때는 예외처리(try-catch)를 해야한다. 이름으로 값을 찾는것
        String dateStr = getDateFormat("yyyy-MM-dd",date);
        //Toast.makeText(DiaryActivity.this, dateStr, Toast.LENGTH_SHORT).show();


        List<HashMap<String,Object>> diaryList = new ArrayList<HashMap<String,Object>>();// 리스트로 받기위함 선언을 한다
        HashMap<String,Object> diaryObj = null; //MAP형태로 저장하기위한 객채 선언
        database = helper.getWritableDatabase();
        if(database !=null){
            String sql = "select diaryId, title, content, imgstr, regdt from " + diaryTableName + " where DATE(regdt)='"+dateStr+"'";
            Cursor cursor = database.rawQuery(sql, null);   // select 사용시 사용(sql문, where조건 줬을 때 넣는 값)
            Log.d("ddroad","조회된 데이터 개수 : " + cursor.getCount());   // db에 저장된 행 개수를 읽어온다

            if (cursor != null && cursor.moveToFirst()){
                do {

                    int diaryId = cursor.getInt(0);   // 첫번째 속성
                    String title = cursor.getString(1); // 두번째 속성
                    String content = cursor.getString(2);    // 세번째 속성
                    String imgstr = cursor.getString(3);    // 세번째 속성
                    String regdt = cursor.getString(4);    // 세번째 속성

                    diaryObj = new HashMap<String,Object>(); //데이터를 넣기 위해 생성자 생성
                    diaryObj.put("diaryId",diaryId);
                    diaryObj.put("title",title);
                    diaryObj.put("content",content);
                    diaryObj.put("imgstr",imgstr);
                    diaryObj.put("regdt",regdt);

                    diaryList.add(diaryObj);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        return diaryList;//최종 데이터를 리턴 한다
    }

    //혹시 몰라서 추가함
    private String getNow(String format){//현재 날짜를 포팻 형태로 String 출력

        if(format == null || format ==""){
            format  = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateFormat(String format,Date date){//입력 Date를 날짜를  포팻 형태로 String 출력

        if(format == null || format ==""){
            format  = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(date);
    }

    public void onResume() {
        super.onResume();
        selectDiaryListView(mCurrentDate);
        caldroidFragment.refreshView();
    }


}