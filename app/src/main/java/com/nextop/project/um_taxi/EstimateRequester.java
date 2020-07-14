package com.nextop.project.um_taxi;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.nextop.project.um_taxi.dto.EstimateResultDto;
import com.nextop.project.um_taxi.models.AddressModel;
import com.nextop.project.um_taxi.models.DisplayItem;

public class EstimateRequester implements Runnable {



    private Handler handler;
    private Double startLatitude; //내 위치
    private Double startLongitude;
    private Double endLatitude; //가고 싶은데 위치
    private Double endLongitude;

    EstimateRequester(double startLatitude, double startLongitude, double endLatitude, double endLongitude , Handler handler){
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
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
            String urlString = String.format("http://192.168.0.19:8080/estimate?startLatitude=%s&startLongitude=%s&endLatitude=%s&endLongitude=%s"
                    ,this.startLatitude.toString(),this.startLongitude.toString(),this.endLatitude.toString(),this.endLongitude.toString());
            GenericUrl url = new GenericUrl(urlString);
            HttpHeaders headers = new HttpHeaders();
            //headers.setAuthorization("KakaoAK bb2f0216caf115b2348f23a520d32418");
            HttpRequest request = requestFactory.buildGetRequest(url).setHeaders(headers);

            EstimateResultDto estimateResult = request.execute().parseAs(EstimateResultDto.class);


            Message message = this.handler.obtainMessage();
            message.obj = estimateResult;
            //message.obj = addressModel;
            this.handler.sendMessage(message);

        } catch (Exception ex) {
            Log.e("HTTP_REQUEST", ex.toString());
        }

    }
}
