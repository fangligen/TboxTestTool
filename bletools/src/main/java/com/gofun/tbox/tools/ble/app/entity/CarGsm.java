package com.gofun.tbox.tools.ble.app.entity;

public class CarGsm {
    private String name;
    private String value;

    private int gsm_status;
    private int gsm_rssi;
    private String gsm_lac;
    private String gsm_cell_id;

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
