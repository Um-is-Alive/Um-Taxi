package com.nextop.project.um_taxi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextop.project.um_taxi.dto.EstimateResultDto;
import com.nextop.project.um_taxi.dto.MatchDto;
import com.nextop.project.um_taxi.dto.MatchResultDto;
import com.nextop.project.um_taxi.location.MapEventListener;
import com.nextop.project.um_taxi.models.AddressModel;
import com.nextop.project.um_taxi.models.DisplayItem;
import com.nextop.project.um_taxi.models.Document;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.regex.MatchResult;


public class MainActivity extends AppCompatActivity {



    private static final int PERMISSION_REQUEST_LOCATION = 10001; //요청 코드
    private LocationManager locationManager; //
    private LocationListener locationListener;
    private MapEventListener mapEventListener;
    private boolean isSelectDeparture =true;
    private boolean isSelectDone =false;
    private MatchDto match = new MatchDto();
    private double latitude = 37.5571992;
    private double longitude = 126.970536;

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getAppKeyHash();
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

    private Handler makeEstimateHandler(){
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                EstimateResultDto result = (EstimateResultDto) msg.obj;
                TextView distance = findViewById(R.id.estimate_distance);
                TextView time = findViewById(R.id.estimate_time);
                TextView cost = findViewById(R.id.estimate_cost);
                DecimalFormat distanceFormat = new DecimalFormat("##.00km");
                distance.setText(distanceFormat.format(result.estimateDistance));
                time.setText(result.estimateTime + "분");
                cost.setText(result.estimateCost+"원");

                LinearLayout matchView = findViewById(R.id.match_view);
                matchView.setVisibility(View.VISIBLE);

            }
        };
    }


    private Handler makeHandler() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                TextView position;
                if(isSelectDone) return;
                DisplayItem displayItem = (DisplayItem) msg.obj;
                AddressModel address = displayItem.addressModel;

                if(isSelectDeparture){
                    position = findViewById(R.id.departure);
                    match.startLatitude = displayItem.latitude;
                    match.startLongitude = displayItem.longitude;
                } else {
                    position = findViewById(R.id.arrival);
                    match.endLatitude = displayItem.latitude;
                    match.endLongitude = displayItem.longitude;
                }


                if(address.documents.size() > 0) {
                    Document document = address.documents.get(0); //road address 도로명
                    if (document.roadAddress != null && document.roadAddress.addressName != null) {
                        if(document.roadAddress.buildingName != null && document.roadAddress.buildingName.length() > 0){
                            position.setText(document.roadAddress.buildingName);
                        } else {
                            position.setText(document.roadAddress.addressName);
                        }
                    } else {
                        position.setText(document.address.addressName);
                    }
                }

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


    public void toggleDepartureSelect(View clicked) {
        LinearLayout estimateView = findViewById(R.id.estimate_view);
        estimateView.setVisibility(View.GONE);
        this.isSelectDone = false;
        Button arrivalButton = findViewById(R.id.arrival_select);
        Button arrivalResetButton = findViewById(R.id.arrival_select_reset);
        if(clicked.getId() == R.id.departure_select){
            this.isSelectDeparture = false;
            Button resetButton = findViewById(R.id.arrival_select_reset);
            resetButton.setVisibility(View.VISIBLE);
            arrivalButton.setVisibility(View.VISIBLE);
            clicked.setVisibility(View.GONE);
        } else {
            this.isSelectDeparture = true;
            Button departureButton = findViewById(R.id.departure_select);
            departureButton.setVisibility(View.VISIBLE);
            arrivalButton.setVisibility(View.INVISIBLE);
            arrivalResetButton.setVisibility(View.GONE);
            clicked.setVisibility(View.GONE);
        }
    }

    public void toggleArrivalSelect(View clicked) {
        LinearLayout estimateView = findViewById(R.id.estimate_view);
        if(clicked.getId() == R.id.arrival_select){
            this.isSelectDone = true;
            Button arrivalButton = findViewById(R.id.arrival_select_reset);
            arrivalButton.setVisibility(View.VISIBLE);
            clicked.setVisibility(View.GONE);
            estimateView.setVisibility(View.VISIBLE);
        }
        else {
            this.isSelectDone =false;
            Button arrivalButton = findViewById(R.id.arrival_select);
            arrivalButton.setVisibility(View.VISIBLE);
            clicked.setVisibility(View.GONE);
            estimateView.setVisibility(View.GONE);
        }
    }

    public void estimate(View view) {
        Handler handler = this.makeEstimateHandler();
        EstimateRequester requester = new EstimateRequester(
                match.startLatitude,
                match.endLongitude,
                match.endLatitude,
                match.endLongitude,
                handler);
        Thread request = new Thread(requester);
        request.start();
    }

    private Handler matchMatchHandler(){
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg){
                super.handleMessage(msg);
                MatchResultDto result = (MatchResultDto) msg.obj;
                TextView matchDriver = findViewById(R.id.match_driver);
                TextView matchTaxi = findViewById(R.id.arrival_time);
                TextView arrivalTime = findViewById(R.id.arrival_time);
                matchDriver.setText(result.driver);
                matchTaxi.setText(result.taxiNumber);
                arrivalTime.setText(result.arrivalTime+"분");


            }
        };
    }



    private Handler makeMatchFailedHandler(){
        final Activity activity = this;
        return new Handler(){
            @Override
            public void handleMessage (@NonNull Message msg) {
                super.handleMessage(msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                switch (msg.what){
                    case 401:
                        builder.setTitle("인증 실패").setMessage("올바른 사용자가 아닙니다.");
                        break;
                    case 404:
                        builder.setTitle("제품 실패").setMessage("배정 가능한 택시가 없습니다.");
                        break;
                    default:
                        builder.setTitle("오류").setMessage("오류가 발생했습니다.");
                        break;
                }
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    public void match(View view){
        Handler handler = this.matchMatchHandler();
        Handler failHandler = this.makeMatchFailedHandler();
        this.match.limitTime = 5;
        this.match.userId =46;
        MatchRequester requester = new MatchRequester(this.match, handler,failHandler);
        Thread request = new Thread(requester) ;
        request.start();
    }
}
