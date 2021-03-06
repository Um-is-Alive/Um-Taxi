package com.nextop.project.um_taxi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nextop.project.um_taxi.dto.EstimateResultDto;

public class EstimateRequester implements Runnable {
    private Handler handler;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;


    public EstimateRequester(Double startLatitude, Double startLongitude, Double endLatitude, Double endLongitude, Handler handler) {
        this.handler = handler;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
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
            String urlString = String.format("http://192.168.35.29:8080/estimate?startLatitude=%s&startLongitude=%s&endLatitude=%s&endLongitude=%s",
                    this.startLatitude.toString(), this.startLongitude.toString(), this.endLatitude.toString(), this.endLongitude.toString());
            GenericUrl url = new GenericUrl(urlString);
            HttpRequest request = requestFactory.buildGetRequest(url);
            EstimateResultDto estimateResult = request.execute().parseAs(EstimateResultDto.class);

            Message message = this.handler.obtainMessage();
            message.obj = estimateResult;
            this.handler.sendMessage(message);

        } catch (Exception ex) {
            Log.e("HTTP_REQUEST", ex.toString());
        }
    }
}
