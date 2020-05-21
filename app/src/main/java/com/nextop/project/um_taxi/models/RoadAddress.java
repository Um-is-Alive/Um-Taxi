package com.nextop.project.um_taxi.models;

import com.google.api.client.util.Key;

public class RoadAddress {
    @Key("address_name") public String address;
    @Key("building_name") public String buildingName;
}
