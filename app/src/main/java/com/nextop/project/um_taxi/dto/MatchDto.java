package com.nextop.project.um_taxi.dto;

public class MatchDto {

    public int userId;
    public Double startLatitude; //내 위치
    public Double startLongitude;
    public Double endLatitude; //가고 싶은데 위치
    public Double endLongitude;
    public int limitTime;
}
