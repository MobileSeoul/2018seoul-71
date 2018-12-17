package com.seoul.ddroad.map;

import android.util.Log;

import com.seoul.ddroad.R;

import java.io.InputStream;
import java.util.ArrayList;

public class FillData {

    private ArrayList<Data> cafeList, hospitalList, hotelList, salonList, trailList;
    Data data;

    public FillData() {
        cafeList = new ArrayList<Data>();
        hotelList = new ArrayList<Data>();
        hospitalList = new ArrayList<Data>();
        salonList = new ArrayList<Data>();
        trailList = new ArrayList<Data>();
        data = new Data();
    }

    // 7줄씩 끊어서 data객체에 넣기
    public void setData(String type, String str[]) {
        for (int i = 0; i < str.length; i++) {
            switch (i % 7) {
                case 0:
                    data.setTitle(str[i]);
                    break;
                case 1:
                    data.setLink(str[i]);
                    break;
                case 2:
                    data.setDetail(str[i]);
                    break;
                case 3:
                    data.setTel(str[i]);
                    break;
                case 4:
                    data.setAddress(str[i]);
                    break;
                case 5:
                    data.setLatitude(new Double(str[i]));
                    break;
                case 6:
                    data.setLongitude(new Double(str[i]));
                    break;
            }

            // data객체를 값에 맞는 ArrayList에 넣기
            if (i != 0 && (i + 1) % 7 == 0) {
                switch (type) {
                    case "cafe":
                        cafeList.add(data);
                        break;
                    case "hospital":
                        hospitalList.add(data);
                        break;
                    case "hotel":
                        hotelList.add(data);
                        break;
                    case "salon":
                        salonList.add(data);
                        break;
                    case "trail":
                        trailList.add(data);
                        break;
                }
                data = new Data();
            }
        }

        saveDataSet(type);
    }

    // 받아온 data를 저장한 ArrayList를 각 static Data에 넣기
    public void saveDataSet(String type) {
        switch (type) {
            case "cafe":
                DataSet.cafeList = cafeList;
                break;
            case "hospital":
                DataSet.hospitalList = hospitalList;
                break;
            case "hotel":
                DataSet.hotelList = hotelList;
                break;
            case "salon":
                DataSet.salonList = salonList;
                break;
            case "trail":
                DataSet.trailList = trailList;
                break;
        }
    }
}
