package com.gofun.tbox.tools.ble.transfer.callback;

import android.bluetooth.BluetoothGatt;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.data.BleDevice;
import com.gofun.tbox.tools.ble.transfer.BleConnection;
import com.gofun.tbox.tools.ble.transfer.GofunBleContext;

/**
 * Created by dg on 2018/1/12.
 */

public abstract class OnConnectionListener extends BleGattCallback {

    public abstract void onConnectSucc(BleDevice bleDevice, BluetoothGatt gatt, int status);

    public abstract void onBleDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status);

    @Override
    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status){
        onBleDisConnected(isActiveDisConnected, device, gatt, status);
        //清空
        BleConnection.disconnect();
    }

    @Override
    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        onConnectSucc(bleDevice, gatt, status);
        GofunBleContext.getInstance().setBleMac(bleDevice.getMac());
        GofunBleContext.getInstance().setBleDevice(bleDevice);
        GofunBleContext.getInstance().setConnected(true);
    }
}
