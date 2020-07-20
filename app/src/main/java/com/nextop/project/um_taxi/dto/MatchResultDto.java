package com.nextop.project.um_taxi.dto;

import com.google.api.client.util.Key;

public class MatchResultDto {
    @Key() public String driver;
    @Key() public String taxiNumber;
    @Key() public int arrivalTime;
}
