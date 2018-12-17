package com.seoul.ddroad.diary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SqlLiteDao {
    private SqlLiteHelper helper;
    private SQLiteDatabase database;  // database를 다루기 위한 SQLiteDatabase 객체 생성
    private static String DIARY_DATABASE_NAME = "ddroad.db"; //데이터베이스 이름
    private static final int DATABASE_VERSION = 2;

    public SqlLiteDao(Context context) {
        helper = new SqlLiteHelper(context, // 현재 화면의 context
                DIARY_DATABASE_NAME, // 파일명
                null, // 커서 팩토리
                DATABASE_VERSION); // 버전 번호
    }

    public void insertScreenShot(String imgDir, String content) {
        database = helper.getWritableDatabase();
        int diaryId = 0;
        if (database != null) {

            String sql = "insert into diary(title, content, imgstr, regdt) values('오늘의 산책','" + content + "', '@drawable/diary_walk', datetime('now','localtime'))";
            database.execSQL(sql);

            Cursor cur = database.rawQuery("SELECT MAX(diaryId) FROM diary", null);
            cur.moveToFirst();
            diaryId = cur.getInt(0);
            cur.close();

            if (diaryId > 0) {
                String sqlimg = "insert into diaryimg(diaryId, imgDir) values(?, ?)";
                Object[] params = {diaryId, imgDir};
                database.execSQL(sqlimg, params);
            }
        }
    }


    public void insertDiary(Object[] params) {
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "insert into diary(title, content,imgstr ,regdt) values(?, ?,?,?)";

            database.execSQL(sql, params);


        }
    }

    public void updatDiary(Object[] params) {
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "update diary set title=?, content=?,imgstr=? ,regdt=? where diaryId=?";
            database.execSQL(sql, params);
        }
    }

    public void deleteDiary(int diaryId) {
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "delete from diary where diaryId = ?";
            Object[] params = {diaryId};
            database.execSQL(sql, params);

        }
    }

    public int getLastDiaryId() {
        int ID = 0;
        database = helper.getWritableDatabase();
        if (database != null) {

            Cursor cur = database.rawQuery("SELECT MAX(diaryId) FROM diary", null);
            cur.moveToFirst();
            ID = cur.getInt(0);
            cur.close();
        }
        return ID;
    }

    public HashMap<String, Object> selectDiary(int diaryId) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        database = helper.getWritableDatabase();
        if (database != null) {
            //sqlite에서 값을 가져와서  map 에 담아야합니다~~~~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!꼭 해야봐야함~~~~~~~!!!!!!!!!
            //http://here4you.tistory.com/49 참고 해보세요~ 정말 쉽죠~~
            String sql = "select diaryId, title, content, imgstr, regdt from diary where diaryId = " + diaryId + ";";
            Cursor result = database.rawQuery(sql, null);

            if (result.moveToFirst()) { //커서가 처음지점이 값이있으면,
                int id = result.getInt(0);
                String title = result.getString(1); // 두번째 속성
                String content = result.getString(2);    // 세번째 속성
                String imgstr = result.getString(3);    // 세번째 속성
                String regdt = result.getString(4);    // 세번째 속성

                map.put("diaryId", diaryId);
                map.put("title", title);
                map.put("content", content);
                map.put("imgstr", imgstr);
                map.put("regdt", regdt);

            }
            result.close();

        }
        return map;
    }

    public List<HashMap<String, Object>> selectDiaryList(Date date) {   // 항상 DB문을 쓸때는 예외처리(try-catch)를 해야한다. 이름으로 값을 찾는것
        String dateStr = getDateFormat("yyyy-MM-dd", date);

        List<HashMap<String, Object>> diaryList = new ArrayList<HashMap<String, Object>>();// 리스트로 받기위함 선언을 한다
        HashMap<String, Object> diaryObj = null; //MAP형태로 저장하기위한 객채 선언
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "select diaryId, title, content, imgstr, regdt from diary where DATE(regdt)='" + dateStr + "'";
            Cursor cursor = database.rawQuery(sql, null);   // select 사용시 사용(sql문, where조건 줬을 때 넣는 값)
            Log.d("ddroad", "조회된 데이터 개수 : " + cursor.getCount());   // db에 저장된 행 개수를 읽어온다

            if (cursor != null && cursor.moveToFirst()) {
                do {

                    int diaryId = cursor.getInt(0);   // 첫번째 속성
                    String title = cursor.getString(1); // 두번째 속성
                    String content = cursor.getString(2);    // 세번째 속성
                    String imgstr = cursor.getString(3);    // 세번째 속성
                    String regdt = cursor.getString(4);    // 세번째 속성

                    diaryObj = new HashMap<String, Object>(); //데이터를 넣기 위해 생성자 생성
                    diaryObj.put("diaryId", diaryId);
                    diaryObj.put("title", title);
                    diaryObj.put("content", content);
                    diaryObj.put("imgstr", imgstr);
                    diaryObj.put("regdt", regdt);

                    diaryList.add(diaryObj);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        return diaryList;//최종 데이터를 리턴 한다
    }

    //다이어리 이미지
    public void insertDiaryImg(int diaryId, String imgDir) {
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "insert into diaryimg(diaryId, imgDir) values(?, ?)";
            Object[] params = {diaryId, imgDir};
            database.execSQL(sql, params);
        }
    }

    public void updateDiaryImg(int diaryId, String imgDir) {
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "update diaryimg set imgDir=? where diaryId = ?";
            Object[] params = {imgDir, diaryId};
            database.execSQL(sql, params);
        }
    }

    public void deleteDiaryImg(int diaryId) {
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "Delete from diaryimg where diaryId = ?";
            Object[] params = {diaryId};
            database.execSQL(sql, params);
        }
    }

    public HashMap<String, Object> selectDiaryImg(int diaryId) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        database = helper.getWritableDatabase();
        if (database != null) {
            String sql = "select diaryId, imgDir from diaryimg where diaryId = " + diaryId + ";";
            Cursor result = database.rawQuery(sql, null);

            if (result.moveToFirst()) { //커서가 처음지점이 값이있으면,
                int id = result.getInt(0);
                String imgDir = result.getString(1); // 두번째 속성

                map.put("diaryId", id);
                map.put("imgDir", imgDir);
            }
            result.close();
        }

        return map;
    }

    public int getCountDiaryImg(int diaryId) {
        int cnt = 0;
        database = helper.getWritableDatabase();
        if (database != null) {

            Cursor cur = database.rawQuery("SELECT COUNT(*) FROM diaryimg where diaryId = " + diaryId, null);
            if (cur != null && cur.moveToFirst()) {
                cnt = cur.getInt(0);
            }
            ;

            cur.close();
        }
        return cnt;
    }

    private String getDateFormat(String format, Date date) {//입력 Date를 날짜를  포팻 형태로 String 출력

        if (format == null || format == "") {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        return dateFormat.format(date);
    }
}
