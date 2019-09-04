package com.gofun.tbox.tools.ble.app.control;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.util.Log;
import com.clj.blesample.BleChangeObservable;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.gofun.tbox.tools.ble.transfer.Action;
import com.gofun.tbox.tools.ble.transfer.BleConnection;
import com.gofun.tbox.tools.ble.transfer.EventType;
import com.gofun.tbox.tools.ble.transfer.Request;
import com.gofun.tbox.tools.ble.transfer.callback.OnConnectionListener;
import com.gofun.tbox.tools.ble.transfer.callback.OnResponseListener;
import com.gofun.tbox.tools.ble.transfer.exception.GofunBleException;
import com.gofun.tbox.tools.ble.transfer.msg.base.Message;
import java.util.Observable;
import java.util.Observer;

public class ControlPresenter implements ControlContract.IControlPresenter, Observer {
  static final String TAG = ControlPresenter.class.getSimpleName();
  static final long TIMEOUT = 5 * 1000;
  static final int MSG_TIME_OUT = 1;
  static final int MSG_STRESSING = 2;
  boolean isStressing = false;

  private ControlContract.IControlView iControlView;
  private Activity activity;
  private int successCount = 0, failCount = 0, timeOutCount = 0;

  int stressingIndex = 0;
  static final Action.CarCtrl[] actions = new Action.CarCtrl[] {
      Action.CarCtrl.OPENDOOR_AND_POWERON, Action.CarCtrl.OPENDOOR, Action.CarCtrl.CLOSEDOOR, Action.CarCtrl.POWERON,
      Action.CarCtrl.POWEROFF, Action.CarCtrl.FINDCAR
  };
  static final byte[] btcmds = new byte[] {
      (byte) Request.BLE_CMD_DOWN_GET_KEY, (byte) Request.BLE_CMD_DOWN_GET_KEY, (byte) Request.BLE_CMD_DOWN_CHECK_KEY
  };

  public ControlPresenter(ControlContract.IControlView iControlView, Activity activity) {
    this.iControlView = iControlView;
    this.activity = activity;
    init();
  }

  private void init() {
    BleChangeObservable.getInstance().addObserver(this);
    BleManager.getInstance().init(activity.getApplication());
    BleManager.getInstance().enableLog(true).setMaxConnectCount(1).setOperateTimeout(3000);
    BleConnection.getInstance().addListener(new BleCmdResponseListener());
    getKey();
  }

  @Override public void update(Observable o, Object arg) {
    if (o instanceof BleChangeObservable) {
      iControlView.onDisconnect();
    }
  }

  @Override public void startStressing() {
    isStressing = true;
    handler.sendEmptyMessage(MSG_STRESSING);
  }

  @Override public void stopStressing() {
    handler.removeMessages(MSG_STRESSING);
  }

  @Override public void startConnect(String mac) {
    try {
      BleConnection.connect(mac, new BleConnectListener());
    } catch (GofunBleException e) {
      e.printStackTrace();
    }
  }

  @Override public void disConnect() {
    BleManager.getInstance().disconnectAllDevice();
  }

  @Override public void stopControl() {
    isStressing = false;
  }

  @Override public void sendCmd(Action.CarCtrl action) {
    iControlView.onCmdSend();
    handler.sendEmptyMessageDelayed(MSG_TIME_OUT, TIMEOUT);
    try {
      Request.carctrl(action, new BleWriteCallback() {
        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          Log.i(TAG, "指令下发成功");
        }

        @Override public void onWriteFailure(BleException exception) {
          Log.e(TAG, "指令下发失败");
          failCount = failCount + 1;
          iControlView.reflushResult(successCount, failCount, timeOutCount);
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      handler.removeMessages(MSG_STRESSING);
      iControlView.onConnectStatus(false, null);
    }
  }

  @Override public void getKey() {
    try {
      Request.bleCmdDown((byte) Request.BLE_CMD_DOWN_GET_KEY, new BleWriteCallback() {
        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          Log.e(TAG, "getKey is success");

          iControlView.getKey(true);
          checkKey();
        }

        @Override public void onWriteFailure(BleException exception) {
          Log.e(TAG, "getKey is fail");
          iControlView.getKey(false);
        }
      });
    } catch (GofunBleException e) {
      e.printStackTrace();
    }
  }

  @Override public void checkKey() {
    try {
      Request.bleCmdDown((byte) Request.BLE_CMD_DOWN_CHECK_KEY, new BleWriteCallback() {

        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          Log.e(TAG, "checkKey is success");
          BleConnection.getInstance().openNotify();
          iControlView.checkKey(true);
        }

        @Override public void onWriteFailure(BleException exception) {
          Log.e(TAG, "checkKey is fail");
          iControlView.checkKey(false);
        }
      });
    } catch (GofunBleException e) {
      e.printStackTrace();
    }
  }

  private void sendBleCmdDown(byte cmd) {
    try {
      iControlView.onCmdSend();
      Request.bleCmdDown(cmd, new BleWriteCallback() {
        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          Log.e(TAG, "blecmd is success");
        }

        @Override public void onWriteFailure(BleException exception) {
          Log.e(TAG, "blecmd is fail");
        }
      });
    } catch (GofunBleException e) {
      e.printStackTrace();
    }
  }

  class BleConnectListener extends OnConnectionListener {
    @Override public void onConnectSucc(BleDevice bleDevice, BluetoothGatt gatt, int status) {
      iControlView.onConnectStatus(true, bleDevice);
    }

    @Override public void onBleDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
      Log.e("ttttttt", "onBleDisConnected");
    }

    @Override public void onStartConnect() {

    }

    @Override public void onConnectFail(BleDevice bleDevice, BleException exception) {
      iControlView.onConnectStatus(false, null);
    }
  }

  class BleCmdResponseListener extends OnResponseListener {
    @Override public void onCarCtrlRes(short msgSeq, EventType eventType, byte resultCode) {

    }

    @Override public void onIdCardCtrlRes(short msgSeq, byte resultCode) {

    }

    @Override public void onCarsysReport(Message message) {

    }

    @Override public void onResponse(Message message) {
      super.onResponse(message);
      handler.removeMessages(MSG_TIME_OUT);
      Log.e(TAG, "message is on response");
      if (message == null) {
        Log.e(TAG, "message is null");
      } else {
        Log.e(TAG, "seq:"
            + message.getSeq()
            + " event:"
            + message.getEvent()
            + " preMsg seq:"
            + BleConnection.getTmpMessage().getSeq()
            + " result:"
            + message.getResponseResult());
      }
      if (message.getSeq() == BleConnection.getTmpMessage().getSeq()) {
        if (message.getResponseResult() == 0) {
          successCount = successCount + 1;
        } else {
          failCount = failCount + 1;
        }
      }
      iControlView.onCmdResponse();
      iControlView.reflushResult(successCount, failCount, timeOutCount);
      if (isStressing) {
        handler.sendEmptyMessageDelayed(MSG_STRESSING, TIMEOUT);
      }
    }
  }

  Handler handler = new Handler() {
    @Override public void handleMessage(android.os.Message msg) {
      super.handleMessage(msg);
      switch (msg.what) {
        case MSG_TIME_OUT:
          timeOutCount = timeOutCount + 1;
          iControlView.reflushResult(successCount, failCount, timeOutCount);
          iControlView.onCmdResponse();
          break;
        case MSG_STRESSING:
          sendStressing();
          break;
      }
    }
  };

  private void sendStressing() {
    if (isStressing) {
      if (stressingIndex < 5) {
        sendCmd(actions[stressingIndex]);
      } else {
        sendBleCmdDown(btcmds[stressingIndex - 5]);
      }
      stressingIndex = stressingIndex + 1 > 7 ? 0 : stressingIndex + 1;
    }
  }
}
