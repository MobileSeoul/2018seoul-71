package com.seoul.ddroad.setting;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.seoul.ddroad.MainActivity;
import com.seoul.ddroad.R;
import com.seoul.ddroad.diary.DetailActivity;
import com.seoul.ddroad.diary.SingerItem;
import com.seoul.ddroad.intro.DustFragment;
import com.seoul.ddroad.map.PolylineDialog;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView mDisplayDate;
    private TextView mDisplayDogname;
    private Button btn_CertainDog;
    private EditText edt_dogname;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    ArrayList<ListItem> list = new ArrayList<>();


    // 출처: http://tlshenm.tistory.com/45 [No Job Of Star]
    //public static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ddroad" + File.separator + ".cache"; //캐시 기본폴더


    Bundle bundle = new Bundle(1);
    String input;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        input = "";
        //캐쉬 메모리 체크
        checkFunction();

       /* FileCacheFactory.initialize(getActivity().getApplicationContext(), CACHE_PATH);
        if (!FileCacheFactory.getInstance().has("ddroad"))          // 해당 키의 캐시 디렉토리가 있는지 확인
        {
            FileCacheFactory.getInstance().create("ddroad", 0);     // 캐시디렉토리가 없을경우 만든다.
        }
        mFileCache = FileCacheFactory.getInstance().get("ddroad");  // 해당 파일의 캐시 디렉토리를 가져온다.
*/

        recyclerView = getView().findViewById(R.id.settingRecycler);
        mDisplayDate = getView().findViewById(R.id.text_dog_date);
        //줄 구분선 만들기
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(getActivity().getApplicationContext(), new LinearLayoutManager(getActivity()).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list.add(new ListItem("펫 이름설정", R.drawable.bichon1));
        list.add(new ListItem("D-day설정", R.drawable.bichon1));
        list.add(new ListItem("출처보기", R.drawable.bichon2));
        ListAdapter adapter = new ListAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //Toast.makeText(getActivity().getApplicationContext(), "클릭한 아이템의 이름은 " + list.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                //0.강아지 이름, 1.강아지 데려온 날짜, 3.출처보기
                switch (position) {
                    case 0:
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getLayoutInflater().inflate(R.layout.dialog_mydog, null);


                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        btn_CertainDog = mView.findViewById(R.id.btn_certaindog);
                        edt_dogname = mView.findViewById(R.id.edit_dog_name);
                        mDisplayDogname = mView.findViewById(R.id.text_dog_name);
                        //메모장
                        edt_dogname.setText(getFileDate("dogname"));
                        btn_CertainDog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String input = edt_dogname.getText().toString();

                                //파일에 쓰기
                                setFileData(input,"dogname");

                                dialog.dismiss();
                            }
                        });
                        break;
                    case 1:

                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);

                        //Toast.makeText(getActivity().getApplicationContext(), list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                        DatePickerDialog dialogPicker = new DatePickerDialog(getContext(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener,
                                year,
                                month,
                                day);

                        dialogPicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogPicker.show();
                        break;
                    case 2:
                        AlertDialog.Builder mBuilder_source = new AlertDialog.Builder(getContext());
                        View mView_source = getLayoutInflater().inflate(R.layout.dialog_source, null);


                        mBuilder_source.setView(mView_source);
                        AlertDialog dialog_source = mBuilder_source.create();
                        dialog_source.show();
                        break;
                }
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int nYear = year;
                int nMonth = month;
                int nDayofMonth = dayOfMonth;

                int daysss = 0;
                //확인눌러 데이타  다일로그 받어서 넣어주기
                nMonth = nMonth + 1;
                //Log.d("Setting", "onDateSet data: " + year + "/" + month + "/" + dayOfMonth);
                //값을 더스트 프라그먼트의 화면에 넣어줘야한다
                daysss = countDday(nYear, nMonth, nDayofMonth);

                //입향날짜 입력시 1일
                setFileData(String.valueOf(daysss), "dogdate");

                //Toast.makeText(getActivity().getApplicationContext(), daysss, Toast.LENGTH_SHORT).show();
            }

            public int countDday(int myear, int mmonth, int mday) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
                    //캘린더
                    Calendar todaCal = Calendar.getInstance(); //현재날짜
                    Calendar ddayCal = Calendar.getInstance(); //설정날짜

                    mmonth -= 1;

                    ddayCal.set(myear, mmonth, mday);
                    ddayCal.set(myear, mmonth, mday);// D-day의 날짜를 입력 셋팅해준다
                    Log.e("테스트", simpleDateFormat.format(todaCal.getTime()) + "");
                    Log.e("테스트", simpleDateFormat.format(ddayCal.getTime()) + "");

                    long today = todaCal.getTimeInMillis() / 86400000; //(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
                    long dday = ddayCal.getTimeInMillis() / 86400000;
                    long count = (dday - today) * -1; // 오늘 날짜에서 dday 빼준다
                    return (int) count; // 결과값 반환해준다


                } catch (Exception e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        };
    }
    //파일 저장
    private void setFileData(String s,String textName){
        try {

            FileOutputStream fos = getActivity().openFileOutput
                    (textName+".txt", // 파일명 지정
                            Context.MODE_PRIVATE);// 저장모드
            // Context.MODE_APPEND  //기존에 추가
            PrintWriter out = new PrintWriter(fos,false);
            out.write(s);
            //out.println(s);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }// 출처: http://bitsoul.tistory.com/116 [Happy Programmer~]

    }
    //파일 가져오기
    private String getFileDate(String textName){
        String retStr = "";
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = getActivity().openFileInput(textName+".txt");//파일명
            BufferedReader buffer = new BufferedReader
                    (new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            while (str != null) {
                data.append(str + "\n");
                str = buffer.readLine();
            }
            retStr = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retStr;

    }

    public void checkFunction(){
        int permissioninfo = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
           // Toast.makeText(getActivity(),"SDCard 쓰기 권한 있음",Toast.LENGTH_SHORT).show();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

            }else{
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
        }
    }
}