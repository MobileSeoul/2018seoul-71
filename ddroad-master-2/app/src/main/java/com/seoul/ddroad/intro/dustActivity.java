package com.seoul.ddroad.intro;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.seoul.ddroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class dustActivity extends AppCompatActivity {
    private String inputDate;
    private String inputTime;
    private String inputNx;
    private String inputNy;
    private String location;
    private int dustLocation;
    private Double temperature;
    private int fineDust;
    private TextView text_temperature;
    private TextView text_location;
    private TextView text_finddust;
    private TextView text_dog_date;
    private String findDustResult;
    private String findDustColor;
    private String myDog;
    private int nYear;
    private int nMonth;
    private int nDay;
    private int nDogDate;
    private ImageButton mainDogImg;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inputDate = "";
        inputTime = "";
        inputNx = "";
        inputNy = "";
        location = "금천구";
        dustLocation = 0;
        temperature = 0.0;
        fineDust = 0;
        myDog = "콩이";
        findDustResult = "";
        findDustColor = "";
        nYear = 2018;
        nMonth = 9;
        nDay = 30;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dust_cool);

        //text_location = (TextView) findViewById(R.id.text_location);
        //text_location.setText(location);

        text_temperature = (TextView) findViewById(R.id.text_temperature);

        text_finddust = (TextView) findViewById(R.id.text_finedust);
        text_dog_date = (TextView) findViewById(R.id.text_dog_date);
        mainDogImg = (ImageButton) findViewById(R.id.mainDogImg);
        spinner = findViewById(R.id.spinner);

        //폰트설정
        fontChange();

        //날짜시간설정
        setDateTime();

        //임의로 주소입력
        setNxNy(location);

        //온도API
        setTempApi(inputDate, inputTime, inputNx, inputNy);
        //미세먼지 API
        setDustApi();

        //견주 같이 지내온 날짜
        nDogDate = countDday(nYear, nMonth, nDay);

        //설정 셋팅 데이터 shared 써서 가져오기

        //만약 shared 에서 가져온 정보가 널이 아닐경우 데이터 뿌리기
        if(text_dog_date.toString() == null)
        {
            text_dog_date.setText("옵션 에서 설정 해주세요");
            //옵션에 액티비티 창 뛰우기 설정이랑 이화면이랑 액티비티 정보 공유되어야함
        }
        else
        {
            text_dog_date.setText(myDog + "♡ " + nDogDate + " D-Days");

        }


        setSpinner();


    }

    public void setSpinner() {
        //input array data
        final ArrayList<String> list = new ArrayList<>();
        list.add("서울시");
        list.add("종로구");
        list.add("중구");
        list.add("용산구");
        list.add("성동구");
        list.add("광진구");
        list.add("동대문구");
        list.add("성북구");
        list.add("강북구");
        list.add("도봉구");
        list.add("노원구");
        list.add("서대문구");
        list.add("마포구");
        list.add("양천구");
        list.add("강서구");
        list.add("구로구");
        list.add("금천구");
        list.add("영등포구");
        list.add("동작구");
        list.add("관악구");
        list.add("서초구");
        list.add("강남구");
        list.add("송파구");
        list.add("강남구");


        //배열 어답터 사용
        ArrayAdapter spinnerAdater;
        spinnerAdater = new ArrayAdapter(this, R.layout.custom_simple_dropdown_item_1line, list);
        spinner.setAdapter(spinnerAdater);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(dustActivity.this, "선택된 아이템 :" + spinner.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                location = spinner.getItemAtPosition(position).toString();
                text_location.setText(location);

                //날짜시간설정
                setDateTime();

                //임의로 주소입력
                setNxNy(location);

                //온도API
                setTempApi(inputDate, inputTime, inputNx, inputNy);

                setDustApi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });
    }

    public void fontChange() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "BMJUA_ttf.ttf");

        text_temperature.setTypeface(typeface);
        text_finddust.setTypeface(typeface);
        text_dog_date.setTypeface(typeface);
        //text_location.setTypeface(typeface);
    }


    public void setDateTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        inputDate = dateFormat.format(c.getTime());

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        String formattedHour = hourFormat.format(c.getTime());

        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        String formattedMinute = minuteFormat.format(c.getTime());


        int hour = Integer.parseInt(formattedHour);

        //api는 매시간 40분마다 업데이트되기에 if문처리
        if (Integer.parseInt(formattedMinute) < 41) {
            if (hour / 10 < 1) {
                inputTime = "0" + (hour - 1) + "59";
            } else {
                inputTime = (hour - 1) + "59";
            }
        } else {
            inputTime = formattedHour + formattedMinute;
        }

    }


    public void setNxNy(String location) {

        if (location.equals("서울시")) {
            inputNx = "60";
            inputNy = "127";
            dustLocation = 22;
        } else if (location.equals("종로구")) {
            inputNx = "60";
            inputNy = "127";
            dustLocation = 22;
        } else if (location.equals("중구")) {
            inputNx = "60";
            inputNy = "127";
            dustLocation = 23;
        } else if (location.equals("용산구")) {
            inputNx = "60";
            inputNy = "126";
            dustLocation = 20;
        } else if (location.equals("성동구")) {
            inputNx = "61";
            inputNy = "127";
            dustLocation = 15;
        } else if (location.equals("광진구")) {
            inputNx = "62";
            inputNy = "126";
            dustLocation = 5;
        } else if (location.equals("동대문구")) {
            inputNx = "61";
            inputNy = "127";
            dustLocation = 10;
        } else if (location.equals("중랑구")) {
            inputNx = "62";
            inputNy = "128";
            dustLocation = 24;
        } else if (location.equals("성북구")) {
            inputNx = "61";
            inputNy = "127";
            dustLocation = 16;
        } else if (location.equals("강북구")) {
            inputNx = "61";
            inputNy = "128";
            dustLocation = 2;
        } else if (location.equals("도봉구")) {
            inputNx = "61";
            inputNy = "129";
            dustLocation = 9;
        } else if (location.equals("노원구")) {
            inputNx = "61";
            inputNy = "129";
            dustLocation = 8;
        } else if (location.equals("은평구")) {
            inputNx = "59";
            inputNy = "127";
            dustLocation = 21;
        } else if (location.equals("서대문구")) {
            inputNx = "59";
            inputNy = "127";
            dustLocation = 13;
        } else if (location.equals("마포구")) {
            inputNx = "59";
            inputNy = "127";
            dustLocation = 12;
        } else if (location.equals("양천구")) {
            inputNx = "58";
            inputNy = "126";
            dustLocation = 18;
        } else if (location.equals("강서구")) {
            inputNx = "58";
            inputNy = "126";
            dustLocation = 3;
        } else if (location.equals("구로구")) {
            inputNx = "58";
            inputNy = "125";
            dustLocation = 6;
        } else if (location.equals("금천구")) {
            inputNx = "59";
            inputNy = "124";
            dustLocation = 7;
        } else if (location.equals("영등포구")) {
            inputNx = "58";
            inputNy = "126";
            dustLocation = 19;
        } else if (location.equals("동작구")) {
            inputNx = "59";
            inputNy = "125";
            dustLocation = 11;
        } else if (location.equals("관악구")) {
            inputNx = "59";
            inputNy = "125";
            dustLocation = 4;
        } else if (location.equals("서초구")) {
            inputNx = "61";
            inputNy = "125";
            dustLocation = 14;
        } else if (location.equals("강남구")) {
            inputNx = "61";
            inputNy = "126";
            dustLocation = 0;
        } else if (location.equals("송파구")) {
            inputNx = "62";
            inputNy = "126";
            dustLocation = 17;
        } else if (location.equals("강동구")) {
            inputNx = "62";
            inputNy = "126";
            dustLocation = 1;
        }
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
            long count = dday - today; // 오늘 날짜에서 dday 빼준다
            return (int) count; // 결과값 반환해준다


        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setTempApi(String inputDate, String inputTime, String inputNx, String inputNy) {


        String searchUrl = "http://newsky2.kma.go.kr/service/SecndSrtpdFrcstInfoService2/ForecastGrib?";
        String serviceKey = "ServiceKey=6HQWWR6iibX4NvJpYaP%2BP%2Blivy9AiBISgqmHA%2FxT4vsJKPwxr%2BRIMG%2BNFDhz3ZWkSJHoyDp3cTjzF2dfuXG84w%3D%3D";
        String baseDate = "&base_date=" + inputDate;
        String baseTime = "&base_time=" + inputTime;
        String nx = "&nx=" + inputNx;
        String ny = "&ny=" + inputNy;
        String lastText = "&pageNo=1&numOfRows=10&_type=json";
        String requestUrl = searchUrl + serviceKey + baseDate + baseTime + nx + ny + lastText;

        getJSON(requestUrl, 1);
    }

    public void setDustApi() {

        String searchUrl = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureSidoLIst?";
        String serviceKey = "serviceKey=DgPFrzzGO9aHn5QtBPF80nYkh88aX8rz6DbddIYvZqyKTg1tcBN8c4u5p66zyw5bWdA5xUuBo7pnjzTNOex63w%3D%3D";
        String lastText = "&numOfRows=24&pageSize=10&pageNo=1&startPage=1&sidoName=%EC%84%9C%EC%9A%B8&searchCondition=DAILY&_returnType=json";
        String requestUrl = searchUrl + serviceKey + lastText;

        getJSON(requestUrl, 2);
    }

    public void getJSON(final String requestUrl, final int funcFlag) {

        if (requestUrl == null) return;

        Thread thread = new Thread(new Runnable() {

            public void run() {

                String result;

                try {

                    Log.e("tag", requestUrl);
                    URL url = new URL(requestUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.connect();

                    int responseStatusCode = httpURLConnection.getResponseCode();

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {

                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();

                    }


                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line;


                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    httpURLConnection.disconnect();

                    result = sb.toString().trim();


                } catch (Exception e) {
                    result = e.toString();

                }


                //funcFlag 1은 온도 2는 미세먼지
                if (funcFlag == 1) {
                    temperature = tempJsonParser(result);
                    if (temperature != null) {

                        runOnUiThread(new Runnable() {

                            public void run() {

                                if (temperature != 100.0) {
                                    text_temperature.setText(String.valueOf(temperature) + "도");
                                } else {
                                    text_temperature.setText("시간을 대한민국 기준으로");
                                    text_temperature.setTextColor(Color.parseColor("#FF0000"));
                                }
                            }

                        });

                    }


                } else {

                    fineDust = dustJsonParser(result);
                    if (fineDust != 0) {
                        findDustResult = setFineDustResult(fineDust);


                        runOnUiThread(new Runnable() {

                            public void run() {

                                if (temperature == 100.0) {
                                    text_finddust.setText("재설정해주세요");
                                    text_finddust.setTextColor(Color.parseColor("#FF0000"));
                                    mainDogImg.setImageResource(R.drawable.dogface_2);
                                } else {
                                    text_finddust.setText("미세먼지 " + findDustResult);
                                    //text_finddust.setTextColor(Color.parseColor(findDustColor));
                                    if (findDustResult.equals("나쁨") || findDustResult.equals("매우나쁨")) {
                                        mainDogImg.setImageResource(R.drawable.dogface_2);
                                    } else if (findDustResult.equals("좋음") || findDustResult.equals("보통")) {
                                        mainDogImg.setImageResource(R.drawable.dogface_1);
                                    }
                                }
                            }

                        });
                    }
                }
            }

        });
        thread.start();
    }


    public Double tempJsonParser(String jsonString) {
        double tempResult = 0.0;

        if (jsonString == null) return 0.0;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject res = jsonObject.getJSONObject("response");
            JSONObject body = res.getJSONObject("body");
            JSONObject items = body.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");
            JSONObject jsonObject2 = new JSONObject(item.get(5).toString());
            tempResult = BigDecimal.valueOf(jsonObject2.getDouble("obsrValue")).doubleValue();

            return tempResult;
        } catch (JSONException e) {

            Log.d("TAG", e.toString());
            tempResult = 100.0;

        }

        return tempResult;
    }

    public int dustJsonParser(String jsonString) {
        int dustResult = 0;

        if (jsonString == null) return dustResult;

        try {
            Log.e("dustLocation", String.valueOf(dustLocation));
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray item = jsonObject.getJSONArray("list");
            Log.e("test", item.get(dustLocation).toString());
            JSONObject obj = new JSONObject(item.get(dustLocation).toString());
            //미세먼지만 가져옴 초미세먼지의경우 "pm25Value" 가져오기
            dustResult = obj.getInt("pm10Value");
            return dustResult;
        } catch (JSONException e) {

            Log.d("TAG", e.toString());
        }

        return dustResult;
    }

    //미세먼지 수치&색상 세팅기준
    public String setFineDustResult(int fineDust) {
        String result = "";
        //미세먼지 ~30도 좋음
        if (fineDust < 31) {
            result = "좋음";
            findDustColor = "#2BA5BA";

            //미세먼지 ~80도 보통
        } else if (fineDust < 81) {
            result = "보통";
            findDustColor = "#34862E";

            //미세먼지 ~150도 나쁨
        } else if (fineDust < 151) {
            result = "나쁨";
            findDustColor = "#CD3B3B";

            //미세먼지 150도~ 매우나쁨
        } else if (fineDust > 150) {
            result = "매우나쁨";
            findDustColor = "#ED0000";

        } else {

        }
        return result;
    }
}
