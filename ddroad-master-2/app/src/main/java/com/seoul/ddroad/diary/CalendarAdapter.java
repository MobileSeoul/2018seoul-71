package com.seoul.ddroad.diary;

import android.app.Activity;
import android.content.Context;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.seoul.ddroad.FontsOverride;
import com.seoul.ddroad.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hirondelle.date4j.DateTime;

import static java.sql.DriverManager.println;

public class CalendarAdapter extends CaldroidGridAdapter {

    private String diaryTableName = "diary"; //테이블 이름
    private String diaryDatabaseName = "ddroad.db"; //데이터베이스 이름
    SqlLiteHelper helper;
    SQLiteDatabase database;  // database를 다루기 위한 SQLiteDatabase 객체 생성

    public CalendarAdapter(Context context, int month, int year,
                           Map<String, Object> caldroidData,
                           Map<String, Object> extraData) {
        super(context, month, year, caldroidData, extraData);

        //DB 선언
        helper = new SqlLiteHelper(context, // 현재 화면의 context
                diaryDatabaseName, // 파일명
                null, // 커서 팩토리
                2); // 버전 번호
        database = helper.getWritableDatabase();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cellView = convertView;

            // For reuse
            if (convertView == null) {
                cellView = inflater.inflate(R.layout.calendar_item, null);
            }
          //  FontsOverride.setDefaultFont(context, "MONOSPACE", "font/nanumpen.ttf");
           /* 폰트바꾸는거같음
            if (cellView is  ViewGroup) {
//            context?.updateTextColors(cellView)
                context?.initTextSize(cellView, context)
            FontUtils.setFontsTypeface(context, context.assets, null, cellView)
             }*/

            int topPadding = cellView.getPaddingTop();
            int leftPadding = cellView.getPaddingLeft();
            int bottomPadding = cellView.getPaddingBottom();
            int rightPadding = cellView.getPaddingRight();

            TextView tv1 = cellView.findViewById(R.id.tv1);
            TextView tv2 = cellView.findViewById(R.id.diaryCount);
            ImageView imageView1 = cellView.findViewById(R.id.weather);

            tv1.setTextColor(Color.BLACK);

            // Get dateTime of this cell
            DateTime dateTime = this.datetimeList.get(position);
            Resources resources = context.getResources();

            // Set color of the dates in previous / next month
            if (dateTime.getMonth() != month) {
                tv1.setTextColor(resources
                        .getColor(com.caldroid.R.color.caldroid_darker_gray));
            }

            boolean shouldResetDiabledView = false;
            boolean shouldResetSelectedView = false;

            // Customize for disabled dates and date outside min/max dates
            if (minDateTime != null && dateTime.lt(minDateTime)
                    || maxDateTime != null && dateTime.gt(maxDateTime)
                    || disableDates != null && disableDates.indexOf(dateTime) != -1) {

                tv1.setTextColor(CaldroidFragment.disabledTextColor);
                if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
                } else {
                    cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
                }

                if (dateTime == getToday()) {
                    cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
                }

            } else {
                shouldResetDiabledView = true;
            }

            // Customize for selected dates
            if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
                cellView.setBackgroundResource(R.drawable.bg_card_cell_select_selector);

                tv1.setTextColor(Color.BLACK);

            } else {
                shouldResetSelectedView = true;
            }

            if (shouldResetDiabledView && shouldResetSelectedView) {
                // Customize for today
                if (dateTime == getToday()) {
                    cellView.setBackgroundResource(R.drawable.bg_card_cell_today_selector);
                } else {
                    cellView.setBackgroundResource(R.drawable.bg_card_cell_default);
                }
            }

            tv1.setText(""+dateTime.getDay());

            String dateString = dateTime.format("YYYY-MM-DD");
            int count = 0;

            List<HashMap<String,Object>> diaryList = new ArrayList<HashMap<String,Object>>();// 리스트로 받기위함 선언을 한다
            HashMap<String,Object> diaryObj = null; //MAP형태로 저장하기위한 객채 선언

            if(database !=null){
                String sql = "select diaryId, title, content, imgstr, regdt from " + diaryTableName + " where DATE(regdt)='"+dateString+"'";
                Cursor cursor = database.rawQuery(sql, null);   // select 사용시 사용(sql문, where조건 줬을 때 넣는 값)

                if (cursor != null && cursor.moveToFirst()){
                    count =  cursor.getCount();

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

            boolean initWeather = false;
            if (diaryList.size() > 0) {
                for (int i=0; i < diaryList.size() ; i++){
                    String imgstr = (String)diaryList.get(i).get("imgstr");
                    if(imgstr != null && !"".equals(imgstr)){
                        initWeather = true;
                        String resName = imgstr;
                        String packName = this.context.getPackageName(); // 패키지명
                        int resID = context.getResources().getIdentifier(resName, "drawable", packName);
                        imageView1.setImageResource(resID);
                        break;
                    }
                }


                if (!initWeather) {
                    imageView1.setVisibility(View.GONE);
                    imageView1.setImageResource(0);
                }
            } else {
                imageView1.setVisibility(View.GONE);
                imageView1.setImageResource(0);
            }

            if (count > 0) {

                tv2.setText(""+count+" 건");
                tv2.setTextColor(parent.getResources().getColor(R.color.diaryCountText));
            } else {
                tv2.setText(null);
            }
            // Somehow after setBackgroundResource, the padding collapse.
            // This is to recover the padding
            cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);

            // Set custom color if required
            setCustomResources(dateTime, cellView, tv1);

            return cellView;
        }
    }

}
