package com.nextop.project.um_taxi;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nextop.project.um_taxi.models.AddressModel;
import com.nextop.project.um_taxi.models.DisplayItem;

public class AddressRequester implements Runnable {
    private Handler handler;
    private Double lat;
    private Double lng;

    public AddressRequester(Double lat, Double lng, Handler handler) {
        this.lat = lat;
        this.lng = lng;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(
                    new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new JsonObjectParser(new JacksonFactory()));
                        }
                    });
            String urlString = String.format("https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%s&y=%s", this.lng.toString(), this.lat.toString());
            GenericUrl url = new GenericUrl(urlString);
            HttpHeaders headers = new HttpHeaders();
            headers.setAuthorization("KakaoAK bb2f0216caf115b2348f23a520d32418");
            HttpRequest request = requestFactory.buildGetRequest(url).setHeaders(headers);
            AddressModel addressModel = request.execute().parseAs(AddressModel.class);

            DisplayItem item = new DisplayItem();
            item.addressModel = addressModel;
            item.latitude = this.lat;
            item.longitude = this.lng;


            Message message = this.handler.obtainMessage();
            message.obj = item;
            //message.obj = addressModel;
            this.handler.sendMessage(message);

        } catch (Exception ex) {
            Log.e("HTTP_REQUEST", ex.toString());
        }
    }
}
