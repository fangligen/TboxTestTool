package com.gofun.tbox.tools.ble.app.entity;

public class CarGps {
    private String name;
    private String value;
    private int gps_status;
    private String gps_speed;
    private String gps_longitude;//经度
    private String gps_latitude; //纬度

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
