package com.nextop.project.um_taxi.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.nextop.project.um_taxi.MainActivity;
import com.nextop.project.um_taxi.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapLocationListener implements LocationListener {

    private MapView mapView;
    private double latitude;
    private double longitude;


    public MapLocationListener(MapView map) { this.mapView = map; }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //내 위치로 지도 이동
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        MapPOIItem marker = new MapPOIItem();
        makermMove(marker);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void makermMove(MapPOIItem marker){
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
        marker = new MapPOIItem();
        marker = mapView.findPOIItemByTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
    }

}
