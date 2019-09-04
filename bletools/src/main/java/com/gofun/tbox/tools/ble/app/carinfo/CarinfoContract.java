package com.gofun.tbox.tools.ble.app.carinfo;

public interface CarinfoContract {
  interface ICarInfoView {
    void reflashCarInfo(String[] data, String[] unit);

    void reflashCarstatus(String[] data);

    void reflashCarGsm(String[] data);

    void reflashCarGps(String[] data);

    void reflashResultCount(int car, int gps, int gms);

    void onDisconnect();
  }

  interface ICarInfoPresenter {
    void sendBleCmd(byte cmd);
  }
}
