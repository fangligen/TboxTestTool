package com.gofun.tbox.tools.ble.transfer;

import com.clj.fastble.data.BleDevice;

/**
 * 保存ble请求过程中会话
 * Created by dg on 2018/1/8.
 */

public class GofunBleContext {

    private static GofunBleContext instance;
    private static final String serviceUUID =  "0000fff0-0000-1000-8000-00805f9b34fb";//服务
    private static final String characterUUID = "0000fff6-0000-1000-8000-00805f9b34fb";//特征值
    private boolean connected;//是否与蓝牙相连
    private String bleMac;//蓝牙mac

    private GofunBleContext(){}

    public static GofunBleContext getInstance(){
        if(instance == null){
            synchronized (GofunBleContext.class){
                if(instance == null){
                    instance = new GofunBleContext();
                }
            }
        }
        return instance;
    }

    //保持ble设备引用
    private BleDevice bleDevice;


    public BleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(BleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public static String getServiceUUID() {
        return serviceUUID;
    }

    public static String getCharacterUUID() {
        return characterUUID;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getBleMac() {
        return bleMac;
    }

    public void setBleMac(String bleMac) {
        this.bleMac = bleMac;
    }
}
