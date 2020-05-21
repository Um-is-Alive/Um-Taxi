package com.nextop.project.um_taxi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;

import java.security.MessageDigest;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;

import com.nextop.project.um_taxi.location.MapEventListener;
import com.nextop.project.um_taxi.location.MapLocationListener;
import com.nextop.project.um_taxi.models.AddressModel;
import com.nextop.project.um_taxi.models.Document;


public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_LOCATION = 10001; //요청 코드
    private LocationManager locationManager; //
    private LocationListener locationListener;
    private double latitude = 37.5571992;
    private double longitude = 126.970536;
    private MapEventListener mapEventListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        // 안드로이드에서 권한 확인이 의무화 되어서 작성된 코드! 개념만 이해
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
                return;
            }
        }


        Location loc = this.locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();

        MapView mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        //내 위치로 지도 이동
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationListener = new MapLocationListener(mapView);
        //locationListener = new MapEventListener(mapView);

        makerShow(mapView); //마커 표시 메소드
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 1, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 1, locationListener);
        Handler handler = makeHandler();
        this.mapEventListener = new MapEventListener(handler);
        mapView.setMapViewEventListener(this.mapEventListener);
    }

    void makerShow(MapView mapView){


        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
    }
    private Handler makeHandler() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                AddressModel address = (AddressModel) msg.obj;
                TextView Address = (TextView) findViewById(R.id.address);
                //TextView buildingname = (TextView) findViewById(R.id.);
                Document document = address.documents.get(0);
                Address.setText(document.roadAddress.address);
                //buildingname.setText(document.roadAddress.buildingName);
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 승인이 된 경우 다시 그리기
                    recreate();
                } else {
                    // 권한 승인이 안 된 경우 종료
                    finish();
                }
                break;
            default:
                break;
        }
    }


}
