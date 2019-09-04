package com.gofun.tbox.tools.ble.app.entity;

public class CarInfo {
    private String name;
    private String value;
    private String unit;

    private int powerData;
    private int voltage;
    private int remainMil;
    private int carODO;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
