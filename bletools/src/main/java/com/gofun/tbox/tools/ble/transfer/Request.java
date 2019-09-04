package com.gofun.tbox.tools.ble.transfer;

import android.util.Log;
import com.clj.fastble.callback.BleWriteCallback;
import com.gofun.tbox.tools.ble.transfer.exception.GofunBleException;
import com.gofun.tbox.tools.ble.transfer.msg.CarCtrlContentDown;
import com.gofun.tbox.tools.ble.transfer.msg.base.Message;
import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;

/**
 * Created by dg on 2018/1/13.
 */

public class Request {
  public static final int BLE_CMD_DOWN_GET_KEY = 0;
  public static final int BLE_CMD_DOWN_CHECK_KEY = 1;
  public static final int BLE_CMD_DOWN_RETURNN_CAR = 2;
  public static final int BLE_CMD_DOWN_CARSYS_START = 3;
  public static final int BLE_CMD_DOWN_CARSYS_STOP = 4;

  public static void carctrl(Action.CarCtrl action, BleWriteCallback callback) throws GofunBleException {
    if (GofunBleContext.getInstance().getBleDevice() == null) {
      throw new GofunBleException(ResultCode.NOT_CONNECTED);
    }
    Message reqMsg = new Message();
    CarCtrlContentDown content = new CarCtrlContentDown();
    content.setCmd(action.getCmd());
    reqMsg.setContent(content);
    reqMsg.setEvent(EventType.CMD_CARCTROL.getEvent());
    BleConnection.request(reqMsg, callback);
  }

  public static void bleCmdDown(byte action, BleWriteCallback callback) throws GofunBleException {
    if (GofunBleContext.getInstance().getBleDevice() == null) {
      throw new GofunBleException(ResultCode.NOT_CONNECTED);
    }
    Message reqMsg = new Message();
    MessageContent content = new MessageContent();
    if (action == BLE_CMD_DOWN_GET_KEY) {
      byte[] data = new byte[1];
      data[0] = 0x01;
      content.setContent(data);
      reqMsg.setContent(content);
      Log.e("BLE_APP", "key get event: " + EventType.CMD_KEYGET.getEvent());
      reqMsg.setEvent(EventType.CMD_KEYGET.getEvent());
    } else if (action == BLE_CMD_DOWN_CHECK_KEY) {
      byte[] data = new byte[2];
      data[0] = 0x01;
      data[1] = 0x01;
      content.setContent(data);
      reqMsg.setContent(content);
      reqMsg.setEvent(EventType.CMD_KEYCHECK.getEvent());
    } else if (action == BLE_CMD_DOWN_RETURNN_CAR) {
      byte[] data = new byte[2];
      data[0] = 0x02;
      content.setContent(data);
      reqMsg.setContent(content);
      reqMsg.setEvent(EventType.CMD_RETURNCAR.getEvent());
    } else if (action == BLE_CMD_DOWN_CARSYS_START) {
      byte[] data = new byte[1];
      data[0] = 0x00;
      content.setContent(data);
      reqMsg.setContent(content);
      reqMsg.setEvent(EventType.CMD_CARSYS_INFO.getEvent());
    } else if (action == BLE_CMD_DOWN_CARSYS_STOP) {
      byte[] data = new byte[1];
      data[0] = 0x01;
      content.setContent(data);
      reqMsg.setContent(content);
      reqMsg.setEvent(EventType.CMD_CARSYS_INFO.getEvent());
    }
    BleConnection.request(reqMsg, callback);
  }
}
