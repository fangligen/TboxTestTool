package com.gofun.tbox.tools.ble.app.scanning;

import com.clj.fastble.data.BleDevice;
import java.util.List;

public interface ScanningContract {

  interface IScanningView {
    void startScanning();

    void startConnect();

    void onConnectSuccess(BleDevice bleDevice);

    void onConnectFail();

    void onScanning(BleDevice device);

    void onScanningFinished(List<BleDevice> bleDevices);
  }

  interface IScanningPresenter {

    void stopScanning();

    void startScanning();

    void startConnect(BleDevice device);

    void setScanRule(String uuid, String name, String mac, boolean autoConnect);
  }
}
