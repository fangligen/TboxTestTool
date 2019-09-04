package com.gofun.tbox.tools.ble.app.scanning;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.text.TextUtils;
import android.util.Log;
import com.clj.blesample.BleChangeObservable;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.gofun.tbox.tools.ble.transfer.BleConnection;
import com.gofun.tbox.tools.ble.transfer.GofunBleContext;
import com.gofun.tbox.tools.ble.transfer.callback.OnConnectionListener;
import com.gofun.tbox.tools.ble.transfer.exception.GofunBleException;
import java.util.List;
import java.util.UUID;

public class ScanningPresenter implements ScanningContract.IScanningPresenter {

  private ScanningContract.IScanningView iScanningView;

  public ScanningPresenter(ScanningContract.IScanningView iScanningView, Activity context) {
    this.iScanningView = iScanningView;
    BleManager.getInstance().init(context.getApplication());
    BleManager.getInstance().enableLog(true).setMaxConnectCount(1).setOperateTimeout(5000);
  }

  @Override public void startScanning() {
    startScan();
  }

  @Override public void startConnect(BleDevice device) {
    BleManager.getInstance().cancelScan();
    connect(device.getMac());
  }

  @Override public void stopScanning() {
    BleManager.getInstance().cancelScan();
  }

  @Override public void setScanRule(String uuid, String name, String mac, boolean autoConnect) {
    String[] uuids;
    if (TextUtils.isEmpty(uuid)) {
      uuids = null;
    } else {
      uuids = uuid.split(",");
    }
    UUID[] serviceUuids = null;
    if (uuids != null && uuids.length > 0) {
      serviceUuids = new UUID[uuids.length];
      for (int i = 0; i < uuids.length; i++) {
        serviceUuids[i] = UUID.fromString(uuids[i]);
      }
    }
    String[] names;
    if (TextUtils.isEmpty(name)) {
      names = null;
    } else {
      names = name.split(",");
    }
    BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder().setServiceUuids(serviceUuids)
        .setDeviceName(true, names)
        .setDeviceMac(mac)
        .setAutoConnect(autoConnect)
        .setScanTimeOut(10000)
        .build();
    BleManager.getInstance().initScanRule(scanRuleConfig);
    BleManager.getInstance().initScanRule(scanRuleConfig);
  }

  private void startScan() {
    //BleManager.getInstance().stopScan();
    BleManager.getInstance().scan(new BleCalllback());
  }

  class BleCalllback extends BleScanCallback {

    @Override public void onScanStarted(boolean success) {
      Log.e("scan", "scanning start");
      iScanningView.startScanning();
    }

    @Override public void onScanning(BleDevice bleDevice) {
      String name = bleDevice.getName();
      String mac = bleDevice.getMac();
      byte[] name_byte;
      byte[] mac_byte;
      if (name != null) {
        name_byte = name.getBytes();
        mac_byte = mac.getBytes();
        if ((name_byte[0] == mac_byte[3]) || name_byte[0] == 'G') {
          iScanningView.onScanning(bleDevice);
        }
      }
    }

    @Override public void onScanFinished(List<BleDevice> scanResultList) {
      Log.e("scan", "scanning finish");
      iScanningView.onScanningFinished(scanResultList);
    }
  }

  private void connect(final String blemac) {
    GofunBleContext.getInstance().setBleMac(blemac);
    try {
      BleConnection.connect(blemac, new OnConnectionListener() {

        @Override public void onStartConnect() {
          iScanningView.startConnect();
        }

        @Override public void onConnectFail(BleDevice bleDevice, BleException exception) {
          iScanningView.onConnectFail();
        }

        @Override public void onConnectSucc(BleDevice bleDevice, BluetoothGatt gatt, int status) {
          iScanningView.onConnectSuccess(bleDevice);
        }

        @Override public void onBleDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
          BleChangeObservable.getInstance().notifyObservers();
        }
      });
    } catch (GofunBleException e) {
      e.printStackTrace();
    }
  }
}
