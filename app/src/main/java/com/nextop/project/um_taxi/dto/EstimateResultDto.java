package com.nextop.project.um_taxi.dto;

import com.google.api.client.util.Key;

public class EstimateResultDto {

    @Key() public double estimateDistance;
    @Key() public int estimateTime;
    @Key() public int estimateCost;
}
