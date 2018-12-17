package com.seoul.ddroad.diary;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SqlLiteHelper extends SQLiteOpenHelper {
    public SqlLiteHelper(Context context, String name,
                         SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        // SQLiteOpenHelper 가 최초 실행 되었을 때
        db.execSQL("CREATE  TABLE IF NOT EXISTS diaryimg(diaryId integer , imgDir text)"); //있나 확인 후 생성
        db.execSQL( "CREATE  TABLE IF NOT EXISTS diary(diaryId integer PRIMARY KEY autoincrement, title text, content text,imgstr text,regdt text)");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists diaryimg");
        db.execSQL("drop table if exists diary");

        onCreate(db); // 테이블을 지웠으므로 다시 테이블을 만들어주는 과정
    }
}
