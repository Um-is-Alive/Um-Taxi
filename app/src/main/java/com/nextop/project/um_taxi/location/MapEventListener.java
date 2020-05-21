package com.nextop.project.um_taxi.location;

import com.nextop.project.um_taxi.AddressRequester;
import com.nextop.project.um_taxi.MainActivity;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import android.os.Handler;


public class MapEventListener implements MapView.MapViewEventListener{
    MapView map;
    Handler handler;
    public MapEventListener(Handler handler) {this.handler = handler;}

    @Override
    public void onMapViewInitialized(MapView mapView) {


    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        if(mapPoint != null)
            mapView.findPOIItemByTag(0).setMapPoint(mapPoint);

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        Thread request = new Thread(new AddressRequester(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude, this.handler));
        request.start();
    }
}
