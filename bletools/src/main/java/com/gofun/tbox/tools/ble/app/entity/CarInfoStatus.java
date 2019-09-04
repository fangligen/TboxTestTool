package com.gofun.tbox.tools.ble.app.entity;

public class CarInfoStatus {
    private String name;
    private String value;

    private Boolean car_acc = false;
    private Boolean car_power = false;
    private Boolean car_power_waste = false;
    private Boolean car_fast_charge = false;
    private Boolean car_slow_charge = false;
    private Boolean car_door1 = false,car_door2 = false,car_door3 = false,car_door4 = false,car_door5 = false;


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
