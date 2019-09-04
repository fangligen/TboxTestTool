package com.gofun.tbox.tools.ble.app.carinfo;

import android.content.Context;
import android.util.Log;
import com.clj.blesample.BleChangeObservable;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.transfer.BleConnection;
import com.gofun.tbox.tools.ble.transfer.EventType;
import com.gofun.tbox.tools.ble.transfer.GofunBleContext;
import com.gofun.tbox.tools.ble.transfer.Request;
import com.gofun.tbox.tools.ble.transfer.callback.OnResponseListener;
import com.gofun.tbox.tools.ble.transfer.msg.base.Message;
import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;
import com.gofun.tbox.tools.ble.transfer.util.ByteUtil;
import java.util.Observable;
import java.util.Observer;

public class CarInfoPresenter implements CarinfoContract.ICarInfoPresenter, Observer {
  static final String TAG = CarInfoPresenter.class.getSimpleName();
  Context context;
  CarinfoContract.ICarInfoView iCarInfoView;
  int carCount, gpsCount, gmsCount;

  public CarInfoPresenter(Context context, CarinfoContract.ICarInfoView iCarInfoView) {
    this.context = context;
    this.iCarInfoView = iCarInfoView;
    BleConnection.getInstance().addListener(new BLEResponseListener());
    BleChangeObservable.getInstance().addObserver(this);
  }

  @Override public void sendBleCmd(byte cmd) {
    bleCmdDown(cmd);
  }

  @Override public void update(Observable o, Object arg) {
    if (o instanceof BleChangeObservable) {
      iCarInfoView.onDisconnect();
    }
  }

  private void bleCmdDown(byte action) {
    if (GofunBleContext.getInstance().isConnected() == false) {
      return;
    }
    if (action == Request.BLE_CMD_DOWN_CARSYS_START) {
      Log.i(TAG, "指令：" + "CARSYS_START");
    } else if (action == Request.BLE_CMD_DOWN_CARSYS_STOP) {
      Log.i(TAG, "指令：" + "CARSYS_STOP");
    }
    try {
      Request.bleCmdDown(action, new BleWriteCallback() {
        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          Log.i(TAG, "指令下发成功");
        }

        @Override public void onWriteFailure(BleException exception) {
          Log.e(TAG, "指令下发失败");
        }
      });
    } catch (Exception e) {
    }
  }

  void parse_carsys_info(Message baseMSG) {
    MessageContent mesCon = baseMSG.getContent();
    byte[] dataBak = mesCon.getContent();
    byte dataLen = baseMSG.getLen();
    byte data[] = new byte[dataLen];
    String uiData;
    System.arraycopy(dataBak, 0, data, 0, dataLen);
    if (baseMSG.getEvent() == EventType.CMD_CARSYS_GPS.getEvent()) {
      byte[] gps_info_byte = new byte[dataLen - 1];
      String gps_info_str;
      String gps_status_str;
      System.arraycopy(data, 1, gps_info_byte, 0, dataLen - 1);
      gps_info_str = new String(gps_info_byte);
      String[] gps_str = gps_info_str.split(",");
      if (data[0] == 0) {
        gps_status_str = "睡眠";
      } else if (data[0] == 1) {
        gps_status_str = "运行";
      } else if (data[0] == 2) {
        gps_status_str = "开启";
      } else if (data[0] == 3) {
        gps_status_str = "关闭";
      } else if (data[0] == 4) {
        gps_status_str = "重启";
      } else {
        gps_status_str = "其他";
      }
      uiData = gps_status_str + "," + gps_str[2] + "," + gps_str[1] + "," + gps_str[0];
      Log.e(TAG, "上报GPS信息:" + uiData);
      iCarInfoView.reflashCarGps(uiData.split(","));
      gpsCount = gpsCount + 1;
    } else if (baseMSG.getEvent() == EventType.CMD_CARSYS_GSM.getEvent()) {
      String str;
      String gsm_status_str;
      byte[] gsm_lac = new byte[2];
      byte[] gsm_cellId = new byte[4];
      System.arraycopy(data, 2, gsm_lac, 0, 2);
      System.arraycopy(data, 4, gsm_cellId, 0, 4);

      if (data[0] == 0) {
        gsm_status_str = "0:断电";
      } else if (data[0] == 1) {
        gsm_status_str = "1:上电";
      } else if (data[0] == 2) {
        gsm_status_str = "2:检查开机";
      } else if (data[0] == 3) {
        gsm_status_str = "3:检查开机字符串";
      } else if (data[0] == 4) {
        gsm_status_str = "4:检查SIM卡";
      } else if (data[0] == 5) {
        gsm_status_str = "5:检查到SIM卡";
      } else if (data[0] == 6) {
        gsm_status_str = "6:注册到网络";
      } else if (data[0] == 7) {
        gsm_status_str = "7:AT命令初始化";
      } else if (data[0] == 8) {
        gsm_status_str = "8:网络连接OK";
      } else {
        gsm_status_str = "9:升级";
      }
      uiData = gsm_status_str + "," + Byte.toString(data[1]);//status + rssi
      str = ByteUtil.bytesToHexString(gsm_lac);
      uiData += "," + ByteUtil.bytesToHexString(gsm_lac);
      uiData += "," + ByteUtil.bytesToHexString(gsm_cellId);
      Log.e(TAG, str + "上报GSM信息:" + uiData);
      iCarInfoView.reflashCarGsm(uiData.split(","));
      gmsCount = gmsCount + 1;
    } else if (baseMSG.getEvent() == EventType.CMD_CARSYS_CAR.getEvent()) {
      String str;
      byte powerData = data[0];
      float voltage = (float) ((float) (((((data[1] & 0x00FF) << 8) | (0x00FF & data[2])))) / 100.00);
      short remainMil = (short) (((data[3] & 0x00FF) << 8) | (0x00FF & data[4]));
      //int carODO = (data[5]<<24) + (data[6]<<16) + (data[7]<<8) + data[8];
      int carODO = (int) (((data[5] & 0x000000FF) << 24) | ((data[6] & 0x000000FF) << 16) | ((data[7] & 0x000000FF) << 8) | (
          0x000000FF
              & data[8]));
      //int carstatus = (data[9]<<24) + (data[10]<<16) + (data[11]<<8) + data[12];
      int carstatus = (int) (((data[9] & 0x000000FF) << 24) | ((data[10] & 0x000000FF) << 16) | ((data[11] & 0x000000FF) << 8) | (
          0x000000FF
              & data[12]));
      uiData = Byte.toString(powerData) + "," + Float.valueOf(voltage) + "," + String.valueOf(remainMil) + "," + String.valueOf(
          carODO);
      iCarInfoView.reflashCarInfo(uiData.split(","), context.getResources().getStringArray(R.array.data_unit));
      Log.e(TAG, "上报CAR信息：" + uiData + " carstatus:" + carstatus);
      if (((carstatus >> 0) & 0x01) > 0) {
        uiData = "true";
      } else {
        uiData = "false";
      }
      for (int i = 0; i < 4; i++) {
        if (((carstatus >> (6 + i)) & 0x01) > 0) {
          uiData += ",true";
        } else {
          uiData += ",false";
        }
      }
      for (int i = 0; i < 5; i++) {
        if (((carstatus >> (1 + i)) & 0x01) > 0) {
          uiData += ",true";
        } else {
          uiData += ",false";
        }
      }
      Log.e(TAG, "==" + uiData);
      iCarInfoView.reflashCarstatus(uiData.split(","));
      carCount = carCount + 1;
    }
    iCarInfoView.reflashResultCount(carCount, gpsCount, gmsCount);
  }

  class BLEResponseListener extends OnResponseListener {
    @Override public void onCarCtrlRes(short msgSeq, EventType eventType, byte resultCode) {
      Log.e(TAG, "控制结果,seq:" + msgSeq + ",action:" + eventType + ",result:" + resultCode);
    }

    @Override public void onIdCardCtrlRes(short msgSeq, byte resultCode) {
      Log.e(TAG, "onIdCardCtrlRes 控制结果,seq:" + msgSeq + ",result:" + resultCode);
    }

    @Override public void onCarsysReport(Message message) {
      if (message.getEvent() == EventType.CMD_CARSYS_INFO.getEvent()) {
      } else {
        parse_carsys_info(message);
      }
    }
  }
}
