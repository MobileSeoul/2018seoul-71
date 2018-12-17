package com.seoul.ddroad.map;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.seoul.ddroad.R;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.seoul.ddroad.diary.SqlLiteDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment implements LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    private static String TAG = MapFragment.class.getSimpleName();
    private GoogleMap googleMap;
    private LatLng SEOUL = new LatLng(37.56, 126.97);
    private MapView mapView;
    private LatLng curLatlng;
    private ArrayList<LatLng> latLngList = new ArrayList<>();
    private Polyline polyline;
    private LocationRequest locRequest;
    private FusedLocationProviderClient fusedLocClient;
    private LocationCallback locCallback, locCallback_walk;
    private Marker marker, marker1, marker2;
    private Button btnPrevious;
    private long start, end;
    private int hour, min;
    private float km;
    private double totDistance = 0;
    private String dogname;
    private BitmapDescriptor marker_dog;

    @BindView(R.id.btn_walk)
    Button btn_walk;
    @BindView(R.id.btn_cafe)
    Button btn_cafe;
    @BindView(R.id.btn_hospital)
    Button btn_hospital;
    @BindView(R.id.btn_hotel)
    Button btn_hotel;
    @BindView(R.id.btn_salon)
    Button btn_salon;
    @BindView(R.id.btn_trail)
    Button btn_trail;
    @BindView(R.id.layout_result)
    RelativeLayout layout_result;
    @BindView(R.id.tv_hour)
    TextView tv_hour;
    @BindView(R.id.tv_minute)
    TextView tv_minute;
    @BindView(R.id.tv_km)
    TextView tv_km;

    public MapFragment() {

    }

    protected void createLocationRequest() {
        locRequest = new LocationRequest();
        locRequest.setInterval(2000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLocationRequest();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.btn_walk)
    void startWalk(View view) {
        view.setSelected(!view.isSelected());
        if (view.isSelected()) { //산책 시작
            latLngList.clear();
            start = System.currentTimeMillis();
            changeCallback(locCallback, locCallback_walk, true);
        } else { //산책 끝
            end = System.currentTimeMillis();

            if (latLngList.size() != 0 && end != start) {
                if (calcResult(latLngList)) {
                    Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.translate);
                    btn_walk.setVisibility(View.GONE);
                    layout_result.setVisibility(View.VISIBLE);
                    layout_result.startAnimation(anim);
                }
            } else
                Toast.makeText(this.getContext(), "산책을 하지 않았습니다", Toast.LENGTH_LONG).show();
            changeCallback(locCallback_walk, locCallback, false);

        }
    }


    private boolean calcResult(ArrayList<LatLng> latLngList) {
        totDistance = 0;
        Log.d(TAG, String.valueOf(latLngList.size()));
        LatLng prev = latLngList.get(0);
        for (LatLng latLng : latLngList) {
            totDistance += SphericalUtil.computeDistanceBetween(prev, latLng);
            prev = latLng;
        }
        totDistance /= 1000;
        if (totDistance <= 0.01) {
            Toast.makeText(this.getContext(), "산책을 하지 않았습니다", Toast.LENGTH_LONG).show();
            return false;
        }
        km = Float.parseFloat(String.format("%.1f", totDistance));
        tv_km.setText(String.valueOf(km));

        long time = (end - start) / 1000 / 60;//분
        if (time >= 60) {
            hour = (int) (time / 60);
            min = (int) (time % 60);
        } else {
            hour = 0;
            min = (int) time;
        }
        tv_hour.setText(String.valueOf(hour));
        tv_minute.setText(String.valueOf(min));
        return true;
    }


    // 콜백 메소드 교체 (경로 그리기 <-> 마커 업데이트만)
    public void changeCallback(LocationCallback callback1, LocationCallback callback2, boolean isWalk) {
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (!isWalk)
            fusedLocClient.removeLocationUpdates(callback1);
        fusedLocClient.requestLocationUpdates(locRequest, callback2, null);
    }


    public void drawPolyline(List<LatLng> pointList) {
        if (polyline != null)
            polyline.remove();
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.argb(255, 107, 190, 212))
                .width(20)
                .addAll(pointList)
                .geodesic(true);
        polyline = googleMap.addPolyline(polylineOptions);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView = (MapView) getView().findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }
        fusedLocClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        setLocCallback();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        startLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        startLocationUpdates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        fusedLocClient.removeLocationUpdates(locCallback);
    }

    //location 콜백메소드 정의
    public void setLocCallback() {
        locCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;
                for (Location location : locationResult.getLocations()) {
                    setCurLatlng(location);
                }
            }
        };

        locCallback_walk = new LocationCallback() { // 산책
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;
                latLngList.add(curLatlng);
                drawPolyline(latLngList);
            }
        };
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocClient.requestLocationUpdates(locRequest, locCallback, null);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnInfoWindowClickListener(this);
        setDefaultLoc(this.getContext());
        Bitmap bitmap_dog = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("marker_dogdog2", "drawable", getContext().getPackageName()));
        bitmap_dog = bitmap_dog.createScaledBitmap(bitmap_dog, 120, 120, false);
        marker_dog = BitmapDescriptorFactory.fromBitmap(bitmap_dog);
        setCurMarker();
    }


    //초기 위치 설정
    public void setDefaultLoc(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location loc = null;

        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (loc != null)
            curLatlng = new LatLng(loc.getLatitude(), loc.getLongitude());
        else
            curLatlng = SEOUL;
    }


    //위치 바뀔 때
    @Override
    public void onLocationChanged(Location location) {
        curLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        setCurMarker();
    }

    // set current latlng
    public void setCurLatlng(Location location) {
        curLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        setCurMarker();
    }

    // update current marker position
    public void setCurMarker() {
        if (marker != null)
            marker.setPosition(curLatlng);
        else {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(curLatlng)
                    .icon(marker_dog);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16));
            marker = googleMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }
    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @OnClick(R.id.btn_ok)
    public void clickOk() {
        layout_result.setVisibility(View.GONE);
        btn_walk.setVisibility(View.VISIBLE);
        latLngList.clear();
        if (polyline != null)
            polyline.remove();
    }

    @OnClick(R.id.btn_capture)
    public void clickCapture() {
        setCaptureMarker();

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                String imgPath = screenshot(bitmap);
                String content = "\n";
                dogname = getDogName();
                if (!dogname.equals(""))
                    content += (dogname + "와 ");
                if (hour > 0)
                    content += String.valueOf(hour) + "시간 ";
                content = content + String.valueOf(min) + "분 동안 " + String.valueOf(km) + "km 걸었어요";
                SqlLiteDao sqlDao = new SqlLiteDao(getContext());
                sqlDao.insertScreenShot(imgPath, content);
            }
        };
        googleMap.snapshot(callback);

        layout_result.setVisibility(View.GONE);
        btn_walk.setVisibility(View.VISIBLE);
        Toast.makeText(this.getContext(), "저장되었습니다", Toast.LENGTH_LONG).show();
        if (latLngList.size() > 0)
            latLngList.clear();
        if (marker1 != null) {
            marker1.remove();
            marker2.remove();
        }
        if (polyline != null)
            polyline.remove();
    }


    public String screenshot(Bitmap captureBitmap) {
        FileOutputStream fos;
        File file = new File(this.getContext().getFilesDir(), "CaptureDir"); // 폴더 경로
        Log.d(TAG, this.getContext().getFilesDir().toString());
        if (!file.exists()) {  // 해당 폴더 없으면 만들어라
            file.mkdirs();
        }

        String strFilePath = file + "/" + "test" + ".png";
        File fileCacheItem = new File(strFilePath);
        try {
            fos = new FileOutputStream(fileCacheItem);
            captureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return strFilePath;
    }

    private void setCaptureMarker() {
        if (totDistance >= 0.1) {
            marker.remove();
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("marker_home", "drawable", getContext().getPackageName()));
            bitmap1 = bitmap1.createScaledBitmap(bitmap1, 120, 120, false);
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("marker_dog", "drawable", getContext().getPackageName()));
            bitmap2 = bitmap2.createScaledBitmap(bitmap2, 120, 120, false);
            MarkerOptions markerOptions1 = new MarkerOptions()
                    .position(latLngList.get(0))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap1));
            MarkerOptions markerOptions2 = new MarkerOptions()
                    .position(latLngList.get(latLngList.size() - 1))
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap2));

            marker1 = googleMap.addMarker(markerOptions1);
            marker2 = googleMap.addMarker(markerOptions2);
        }
    }


    @OnClick({R.id.btn_cafe, R.id.btn_hotel, R.id.btn_hospital, R.id.btn_salon, R.id.btn_trail})
    void clickSearch(View view) {
        view.setSelected(!view.isSelected());

        googleMap.clear();
        marker = null;
        setCurMarker();
        if (latLngList.size() != 0)
            drawPolyline(latLngList);

        if (view.isSelected()) {
            if (btnPrevious != null)
                btnPrevious.setSelected(false);
            showMarker(view.getTag().toString());
            btnPrevious = (Button) view;
        } else
            btnPrevious = null;
    }


    private void showMarker(String category) { //버튼 클릭했을 때
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(category, "drawable", getContext().getPackageName()));
        Bitmap bitmap_resize = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
        switch (category) {
            case "marker_cafe":
                for (Data data : DataSet.cafeList)
                    addMarker(data, bitmap_resize);
                break;
            case "marker_hotel":
                for (Data data : DataSet.hotelList)
                    addMarker(data, bitmap_resize);
                break;
            case "marker_hospital":
                for (Data data : DataSet.hospitalList)
                    addMarker(data, bitmap_resize);
                break;
            case "marker_salon":
                for (Data data : DataSet.salonList)
                    addMarker(data, bitmap_resize);
                break;
            case "marker_trail":
                for (Data data : DataSet.trailList)
                    addMarker(data, bitmap_resize);
                break;
        }
    }

    private void addMarker(Data data, Bitmap bitmap) { //마커 추가
        LatLng position = new LatLng(data.getLatitude(), data.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .title(data.getTitle())
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(data);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        DataDialog dialog = new DataDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", (Data) (marker.getTag()));
        args.putParcelable("curLatlng", curLatlng);
        dialog.setArguments(args);
        dialog.show(getActivity().getFragmentManager(), "tag");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);
        return false;
    }

    private String getFileDate(String textName) {
        String retStr = "";
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = getActivity().openFileInput(textName + ".txt");//파일명
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

    private String getDogName() {
        String retStr = "";
        try {
            // 파일에서 읽은 데이터를 저장하기 위해서 만든 변수
            StringBuffer data = new StringBuffer();
            FileInputStream fis = getActivity().openFileInput("dogname.txt");//파일명
            BufferedReader buffer = new BufferedReader
                    (new InputStreamReader(fis));
            String str = buffer.readLine(); // 파일에서 한줄을 읽어옴
            while (str != null) {
                data.append(str);
                str = buffer.readLine();
            }
            retStr = data.toString();
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retStr;

    }

}