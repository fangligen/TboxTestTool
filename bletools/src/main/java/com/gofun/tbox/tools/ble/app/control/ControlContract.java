package com.gofun.tbox.tools.ble.app.control;

import com.clj.fastble.data.BleDevice;
import com.gofun.tbox.tools.ble.transfer.Action;

public interface ControlContract {

  interface IControlView {

    void onCmdSend();

    void onCmdResponse();

    void onConnectStatus(boolean connected, BleDevice bleDevice);

    void reflushResult(int success, int fail, int timeout);

    void getKey(boolean success);

    void checkKey(boolean success);

    void onDisconnect();
  }

  interface IControlPresenter {

    void getKey();

    void checkKey();

    void startConnect(String mac);

    void disConnect();

    void stopControl();

    void sendCmd(Action.CarCtrl action);

    void startStressing();

    void stopStressing();
  }
}
