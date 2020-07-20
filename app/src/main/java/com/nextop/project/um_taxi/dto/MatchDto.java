package com.nextop.project.um_taxi.dto;

import com.google.api.client.util.Key;

public class MatchDto {

    @Key() public int userId;
    @Key() public Double startLatitude; //내 위치
    @Key() public Double startLongitude;
    @Key() public Double endLatitude; //가고 싶은데 위치
    @Key() public Double endLongitude;
    @Key() public int limitTime;
}
