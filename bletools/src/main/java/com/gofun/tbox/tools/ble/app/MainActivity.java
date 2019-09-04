package com.gofun.tbox.tools.ble.app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.app.carinfo.CarinfoActivity;
import com.gofun.tbox.tools.ble.transfer.Action;
import com.gofun.tbox.tools.ble.transfer.BleConnection;
import com.gofun.tbox.tools.ble.transfer.EventType;
import com.gofun.tbox.tools.ble.transfer.GofunBleContext;
import com.gofun.tbox.tools.ble.transfer.Request;
import com.gofun.tbox.tools.ble.transfer.callback.OnConnectionListener;
import com.gofun.tbox.tools.ble.transfer.callback.OnResponseListener;
import com.gofun.tbox.tools.ble.transfer.exception.GofunBleException;
import com.gofun.tbox.tools.ble.transfer.msg.base.Message;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "gofun_ble";
  private static final int MSG_SEND_ACTION = 1;
  private static final int MSG_REGISTER_LISTENER = -1;

  private static final int MSG_ACTION_SEND_SUCCESS = 0;
  private static final int MSG_ACTION_SEND_TIMEOUT = -2;
  private static final int MSG_ACTION_SEND_FAIL = -3;

  private static final int MSG_ACTION_RESPONSE_SUCCESS = 2;
  private static final int MSG_ACTION_RESPONSE_TIMEOUT = -5;
  private static final int MSG_ACTION_RESPONSE_FAIL = -6;
  private static final int MSG_ACTION_REPORT_SUCCESS = 3;
  private static final int MSG_ACTION_CARSYS_SUCCESS = 4;

  private static int Ble_Test_Success_Cnt = 0;
  private static int Ble_Test_Fail_Cnt = 0;
  private static int Ble_Test_Timeout_Cnt = 0;
  private static int Ble_Test_Action = 0;//init,start,pause,stop

  private Button btn_opendoor = null;
  private Button btn_closedoor = null;
  private Button btn_opendoor_poweron = null;
  private Button btn_closedoor_poweroff = null;
  private Button btn_poweroff = null;
  private Button btn_poweron = null;
  private Button btn_disconnect = null;
  private EditText macIput = null;
  private LinearLayout testActionLayout;
  private Button actionConnectBtn = null;
  private Button actionStartBtn = null;
  private Button actionStopBtn = null;
  private Button actionPauseBtn = null;
  private ProgressDialog progressDialog;

  private BleConnection bleConnection = null;

  boolean actionFlag = false, connectSuccess = false;
  long sleepTime = 5 * 1000;
  long BleCmdTimeout = 5 * 1000;

  private static final int REQUEST_CODE_OPEN_GPS = 1;
  private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
  //存ble列表
  private List<BleDevice> bleDevlist = new ArrayList<BleDevice>();
  private BleDevice connectedDev = null;//连接成功后返回的dev
  //private static final String blemac = "3C:A3:08:A5:93:EB"; //tbox2
  //private static final String blemac = "A4:34:F1:78:70:AC";//tbox3
  //private static final String blemac = "EC:24:B8:46:FA:8C";//本人车机2.0
  //private static final String blemac = "88:3F:4A:EB:56:F8";//李超车机2.0
  //private static final String blemac = "A4:34:F1:78:70:AC";
  private static final String blemac = "F0:B5:D1:A3:40:D8";//本人车机2.0
  //    private static final String blemac = "C8:FD:19:49:D1:1F";//蓝牙mac,将来由服务端返回车机蓝牙
  private static final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";//服务
  private static final String characterUUID = "0000fff6-0000-1000-8000-00805f9b34fb";//特征值

  static final Action.CarCtrl[] actions = new Action.CarCtrl[] {
      Action.CarCtrl.OPENDOOR_AND_POWERON, Action.CarCtrl.OPENDOOR, Action.CarCtrl.CLOSEDOOR, Action.CarCtrl.POWERON,
      Action.CarCtrl.POWEROFF
  };
  int actionIndex = 0;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.setTitle("request...");
    initView();

    //初始化BLE连接
    BleManager.getInstance().init(getApplication());
    BleManager.getInstance().enableLog(true).setMaxConnectCount(1).setOperateTimeout(3000);
    BleConnection.getInstance().addListener(listener);
    if (getIntent().getParcelableExtra("device") != null) {
      BleDevice bleDevice = getIntent().getParcelableExtra("device");
      boolean connected = BleManager.getInstance().isConnected(bleDevice);
      enableActionWidget(connected ? 1 : 0);
      if (connected) {
        BleConnection.getInstance().openNotify();
        connectSuccess = true;
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (connectSuccess) {
      enableActionWidget(1);
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    BleConnection.disconnect();
    BleManager.getInstance().destroy();
    if (listener != null) {
      BleConnection.getInstance().removeListener(listener);
    }
  }

  public void sendMessage() {
    Intent intent = new Intent(this, CarinfoActivity.class);
    startActivityForResult(intent, 0);
  }

  private void initView() {
    this.btn_opendoor = this.findViewById(R.id.btn_opendoor);
    this.btn_closedoor =  this.findViewById(R.id.btn_closedoor);
    this.btn_opendoor_poweron =  this.findViewById(R.id.btn_opendoor_poweron);
    this.btn_closedoor_poweroff =  this.findViewById(R.id.btn_closedoor_poweroff);
    this.btn_poweroff =  this.findViewById(R.id.btn_poweroff);
    this.btn_poweron =  this.findViewById(R.id.btn_poweron);
    btn_disconnect = (Button) this.findViewById(R.id.btn_disconnect);

    this.btn_disconnect.setOnClickListener(new Button.OnClickListener() {

      public void onClick(View view) {
        Log.i(TAG, "....正在断开连接");
        BleConnection.disconnect();
        enableActionWidget(0);
        connectSuccess = false;
      }
    });

    this.btn_opendoor.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View view) {
        carctrl(Action.CarCtrl.OPENDOOR);
      }
    });

    this.btn_closedoor.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View view) {
        carctrl(Action.CarCtrl.CLOSEDOOR);
      }
    });

    this.btn_opendoor_poweron.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View view) {
        carctrl(Action.CarCtrl.OPENDOOR_AND_POWERON);
      }
    });

    this.btn_closedoor_poweroff.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View view) {
        carctrl(Action.CarCtrl.CLOSEDOOR_AND_POWEROFF);
      }
    });

    this.btn_poweroff.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View view) {
        carctrl(Action.CarCtrl.POWEROFF);
      }
    });

    this.btn_poweron.setOnClickListener(new Button.OnClickListener() {
      public void onClick(View view) {
        carctrl(Action.CarCtrl.POWERON);
      }
    });
    initMacInput();
    initTestAction();
    enableActionWidget(0);
    Log.e(TAG, "Init Ok");
  }

  private void initMacInput() {
    macIput = (EditText) findViewById(R.id.activity_mac_input);
    actionConnectBtn = (Button) findViewById(R.id.activity_btn_connect);
    actionConnectBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        String mac = macIput.getText().toString();
        if (TextUtils.isEmpty(mac)) {
          macIput.setError("Mac地址不能为空");
        } else {
          connect(mac);
          Log.e(TAG, "connect Ble:" + mac);
        }
      }
    });
    macIput.setText(blemac);
  }

  private void initTestAction() {
    actionStartBtn = (Button) findViewById(R.id.activity_btn_start);
    actionStartBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (connectSuccess) {
          enableActionWidget(2);
          actionFlag = true;
          if (Ble_Test_Action == 3) {
            TextView stv;
            actionIndex = 0;
            Ble_Test_Success_Cnt = 0;
            Ble_Test_Fail_Cnt = 0;
            Ble_Test_Timeout_Cnt = 0;
            stv = (TextView) findViewById(R.id.result_success);
            stv.setText("成功：" + String.valueOf(Ble_Test_Success_Cnt));
            stv = (TextView) findViewById(R.id.result_fail);
            stv.setText("失败：" + String.valueOf(Ble_Test_Fail_Cnt));
            stv = (TextView) findViewById(R.id.result_timeout);
            stv.setText("超时：" + String.valueOf(Ble_Test_Timeout_Cnt));
          }
          Ble_Test_Action = 1;
          sendAction();
        } else {
          Toast.makeText(getApplicationContext(), "请连接设备", Toast.LENGTH_SHORT).show();
        }
      }
    });
    actionPauseBtn = (Button) findViewById(R.id.activity_btn_pause);
    actionPauseBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (connectSuccess) {
          sendMessage();
          actionFlag = false;
          Ble_Test_Action = 2;
          Toast.makeText(getApplicationContext(), "暂停发送命令", Toast.LENGTH_SHORT).show();
          enableActionWidget(3);
        } else {
          Toast.makeText(getApplicationContext(), "请连接设备", Toast.LENGTH_SHORT).show();
        }
      }
    });
    actionStopBtn = (Button) findViewById(R.id.activity_btn_stop);
    actionStopBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        if (connectSuccess) {
          //Ble_Test_Action = 3;
          stopAction();
          enableActionWidget(1);
        } else {
          Toast.makeText(getApplicationContext(), "请连接设备", Toast.LENGTH_SHORT).show();
        }
      }
    });
    testActionLayout = (LinearLayout) findViewById(R.id.activity_action_layout);
    //        testActionLayout.setVisibility(View.GONE);
  }

  private void sendAction() {
    if (actionFlag) {
      if (actionIndex == 0) {
        bleCmdDown((byte) Request.BLE_CMD_DOWN_GET_KEY);
      } else if (actionIndex == 1) {
        bleCmdDown((byte) Request.BLE_CMD_DOWN_CHECK_KEY);
      } else if (actionIndex == 2) {
        carctrl(Action.CarCtrl.OPENDOOR_AND_POWERON);
      } else if (actionIndex == 3) {
        carctrl(Action.CarCtrl.CLOSEDOOR);
      } else if (actionIndex == 4) {
        carctrl(Action.CarCtrl.OPENDOOR);
      } else if (actionIndex == 5) {
        bleCmdDown((byte) Request.BLE_CMD_DOWN_RETURNN_CAR);
      } else if (actionIndex == 6) carctrl(Action.CarCtrl.CLOSEDOOR_AND_POWEROFF);
      if (actionIndex == 6) {
        actionIndex = 0;
      } else {
        actionIndex = actionIndex + 1;
      }
    }
  }

  private void enableActionWidget(int uiStatus) {
    TextView stv;
    if (uiStatus == 0) {//init
      stv = (TextView) findViewById(R.id.activity_btn_status);
      stv.setText("未连接");
      stv.setTextColor(Color.RED);
      macIput.setEnabled(false);
      actionConnectBtn.setEnabled(true);
      actionStartBtn.setEnabled(false);
      actionPauseBtn.setEnabled(true);
      actionStopBtn.setEnabled(false);
      btn_closedoor.setEnabled(false);
      btn_closedoor_poweroff.setEnabled(false);
      btn_disconnect.setEnabled(false);
      btn_opendoor.setEnabled(false);
      btn_opendoor_poweron.setEnabled(false);
      btn_poweroff.setEnabled(false);
      btn_poweron.setEnabled(false);
    } else if (uiStatus == 1) {//connect
      stv = (TextView) findViewById(R.id.activity_btn_status);
      stv.setText("已连接");
      stv.setTextColor(Color.GREEN);
      macIput.setEnabled(false);
      actionConnectBtn.setEnabled(false);
      actionStartBtn.setEnabled(true);
      actionPauseBtn.setEnabled(true);
      actionStopBtn.setEnabled(false);
      btn_closedoor.setEnabled(true);
      btn_closedoor_poweroff.setEnabled(true);
      btn_disconnect.setEnabled(true);
      btn_opendoor.setEnabled(true);
      btn_opendoor_poweron.setEnabled(true);
      btn_poweroff.setEnabled(true);
      btn_poweron.setEnabled(true);
    } else if (uiStatus == 2) {//start
      macIput.setEnabled(false);
      actionConnectBtn.setEnabled(false);
      actionStartBtn.setEnabled(false);
      actionPauseBtn.setEnabled(true);
      actionStopBtn.setEnabled(true);
      btn_closedoor.setEnabled(false);
      btn_closedoor_poweroff.setEnabled(false);
      btn_disconnect.setEnabled(false);
      btn_opendoor.setEnabled(false);
      btn_opendoor_poweron.setEnabled(false);
      btn_poweroff.setEnabled(false);
      btn_poweron.setEnabled(false);
    } else if (uiStatus == 3) {//pause
      macIput.setEnabled(false);
      actionConnectBtn.setEnabled(false);
      actionStartBtn.setEnabled(true);
      actionStopBtn.setEnabled(true);
      actionPauseBtn.setEnabled(false);
      btn_closedoor.setEnabled(false);
      btn_closedoor_poweroff.setEnabled(false);
      btn_disconnect.setEnabled(false);
      btn_opendoor.setEnabled(false);
      btn_opendoor_poweron.setEnabled(false);
      btn_poweroff.setEnabled(false);
      btn_poweron.setEnabled(false);
    } else { //stop
      stv = (TextView) findViewById(R.id.activity_btn_status);
      stv.setText("未连接");
      stv.setTextColor(Color.RED);
      macIput.setEnabled(false);
      actionConnectBtn.setEnabled(true);
      actionStartBtn.setEnabled(false);
      actionPauseBtn.setEnabled(true);
      actionStopBtn.setEnabled(false);
      btn_closedoor.setEnabled(false);
      btn_closedoor_poweroff.setEnabled(false);
      btn_disconnect.setEnabled(false);
      btn_opendoor.setEnabled(false);
      btn_opendoor_poweron.setEnabled(false);
      btn_poweroff.setEnabled(false);
      btn_poweron.setEnabled(false);
    }
  }

  private void stopAction() {
    actionFlag = false;
    Toast.makeText(getApplicationContext(), "停止发送命令", Toast.LENGTH_SHORT).show();
  }

  Handler handler = new Handler() {
    @Override public void dispatchMessage(android.os.Message msg) {
      super.dispatchMessage(msg);
      TextView stv;
      super.dispatchMessage(msg);
      switch (msg.what) {
        case MSG_SEND_ACTION:
          sendAction();
          break;
        case MSG_REGISTER_LISTENER:
          BleConnection.getInstance().openNotify();
          break;
        case MSG_ACTION_SEND_SUCCESS:
          break;
        case MSG_ACTION_SEND_FAIL:
          handler.sendEmptyMessageDelayed(MSG_SEND_ACTION, sleepTime);
          //sendAction();
          break;
        case MSG_ACTION_SEND_TIMEOUT:
          Log.e(TAG, "Send Timeout");
          progressDialog.dismiss();
          handler.sendEmptyMessageDelayed(MSG_SEND_ACTION, sleepTime);
          //sendAction();
          break;
        case MSG_ACTION_RESPONSE_SUCCESS:
          Log.e(TAG, "Response Success");
          handler.sendEmptyMessageDelayed(MSG_SEND_ACTION, sleepTime);
          Ble_Test_Success_Cnt = Ble_Test_Success_Cnt + 1;
          stv = (TextView) findViewById(R.id.result_success);
          stv.setText("成功：" + String.valueOf(Ble_Test_Success_Cnt));
          break;
        case MSG_ACTION_RESPONSE_TIMEOUT:
          progressDialog.dismiss();
          Log.e(TAG, "Response Timeout");
          handler.sendEmptyMessageDelayed(MSG_SEND_ACTION, sleepTime);
          Ble_Test_Timeout_Cnt = Ble_Test_Timeout_Cnt + 1;
          stv = (TextView) findViewById(R.id.result_timeout);
          stv.setText("超時：" + String.valueOf(Ble_Test_Timeout_Cnt));
          //sendAction();
          break;
        case MSG_ACTION_RESPONSE_FAIL:
          Log.e(TAG, "Response Fail");
          handler.sendEmptyMessageDelayed(MSG_SEND_ACTION, sleepTime);
          Ble_Test_Fail_Cnt = Ble_Test_Fail_Cnt + 1;
          stv = (TextView) findViewById(R.id.result_fail);
          stv.setText("失敗：" + String.valueOf(Ble_Test_Fail_Cnt));
          //sendAction();
          break;

        case MSG_ACTION_REPORT_SUCCESS:
          //msg.what = CarinfoActivity.MSG_UPDATE_CARSYS_INFO;
          //CarinfoActivity.handler.sendMessage(msg);
          break;
        case MSG_ACTION_CARSYS_SUCCESS:
          //msg.what = CarinfoActivity.MSG_CARSYS_REPONE;
          //                    CarinfoActivity.handler.sendMessage(msg)
          break;
      }
    }
  };

  private void carctrl(Action.CarCtrl action) {
    if (connectSuccess == false) {
      Toast.makeText(getApplicationContext(), "请连接设备", Toast.LENGTH_SHORT).show();
      return;
    }
    Log.i(TAG, "指令：" + action.name());
    progressDialog.show();
    try {
      Request.carctrl(action, new BleWriteCallback() {

        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          handler.removeMessages(MSG_ACTION_SEND_TIMEOUT);
          Log.i(TAG, "指令下发成功");
          handler.sendEmptyMessageDelayed(MSG_ACTION_RESPONSE_TIMEOUT, BleCmdTimeout);
        }

        @Override public void onWriteFailure(BleException exception) {
          progressDialog.dismiss();
          handler.removeMessages(MSG_ACTION_SEND_TIMEOUT);
          Log.e(TAG, "指令下发失败");
          Toast.makeText(MainActivity.this, "指令下发失败", Toast.LENGTH_SHORT).show();
          handler.sendEmptyMessage(MSG_ACTION_SEND_FAIL);
        }
      });
    } catch (Exception e) {
      progressDialog.dismiss();
      Toast.makeText(MainActivity.this, "err:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    handler.sendEmptyMessageDelayed(MSG_ACTION_SEND_TIMEOUT, BleCmdTimeout);
  }

  private void bleCmdDown(byte action) {
    if (connectSuccess == false) {
      Toast.makeText(getApplicationContext(), "请连接设备", Toast.LENGTH_SHORT).show();
      return;
    }
    if (action == Request.BLE_CMD_DOWN_GET_KEY) {
      Log.i(TAG, "指令：" + "GETKEY");
    } else if (action == Request.BLE_CMD_DOWN_CHECK_KEY) {
      Log.i(TAG, "指令：" + "CHECKKEY");
    } else if (action == Request.BLE_CMD_DOWN_RETURNN_CAR) Log.i(TAG, "指令：" + "RETURNBACKCAR");
    progressDialog.show();
    try {
      Request.bleCmdDown(action, new BleWriteCallback() {

        @Override public void onWriteSuccess(int current, int total, byte[] justWrite) {
          handler.removeMessages(MSG_ACTION_SEND_TIMEOUT);
          Log.i(TAG, "指令下发成功");
          //progressDialog.dismiss();
          handler.sendEmptyMessageDelayed(MSG_ACTION_RESPONSE_TIMEOUT, BleCmdTimeout);
        }

        //@Override public void onWriteSuccess() {
        //
        //}

        @Override public void onWriteFailure(BleException exception) {
          progressDialog.dismiss();
          handler.removeMessages(MSG_ACTION_SEND_TIMEOUT);
          Log.e(TAG, "指令下发失败");
          Toast.makeText(MainActivity.this, "指令下发失败", Toast.LENGTH_SHORT).show();
          handler.sendEmptyMessage(MSG_ACTION_SEND_FAIL);
        }
      });
    } catch (Exception e) {
      progressDialog.dismiss();
      Toast.makeText(MainActivity.this, "err:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
    handler.sendEmptyMessageDelayed(MSG_ACTION_SEND_TIMEOUT, BleCmdTimeout);
  }

  private void checkPermissions() {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (!bluetoothAdapter.isEnabled()) {
      Toast.makeText(this, "请打开蓝牙", Toast.LENGTH_SHORT).show();
      return;
    }

    String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
    List<String> permissionDeniedList = new ArrayList<>();
    for (String permission : permissions) {
      int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
      if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        onPermissionGranted(permission);
      } else {
        permissionDeniedList.add(permission);
      }
    }
    if (!permissionDeniedList.isEmpty()) {
      String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
      ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
    }
  }

  @Override
  public final void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case REQUEST_CODE_PERMISSION_LOCATION:
        if (grantResults.length > 0) {
          for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
              onPermissionGranted(permissions[i]);
            }
          }
        }
        break;
    }
  }

  private void onPermissionGranted(String permission) {
    switch (permission) {
      case Manifest.permission.ACCESS_FINE_LOCATION:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
          new AlertDialog.Builder(this).setTitle(R.string.notifyTitle)
              .setMessage(R.string.gpsNotifyMsg)
              .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  finish();
                }
              })
              .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                  startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                }
              })

              .setCancelable(false)
              .show();
        } else {
          connect(blemac);
        }
        break;
    }
  }

  private boolean checkGPSIsOpen() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    if (locationManager == null) return false;
    return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_OPEN_GPS) {
      if (checkGPSIsOpen()) {
        connect(blemac);
      }
    }
  }

  private void connect(final String blemac) {
    bleConnection = BleConnection.getInstance();
    GofunBleContext.getInstance().setBleMac(blemac);
    try {
      BleConnection.connect(blemac, new OnConnectionListener() {
        @Override public void onStartConnect() {
          Log.i(TAG, "...onStartConnect");
        }

        @Override public void onConnectFail(BleDevice bleDevice, BleException exception) {
          Log.e(TAG, "onConnectFail,error:" + exception.getDescription());
          enableActionWidget(0);
          connectSuccess = false;
          Toast.makeText(MainActivity.this, getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
        }

        //@Override public void onConnectFail(BleException exception) {
        //
        //}

        @Override public void onConnectSucc(BleDevice bleDevice, BluetoothGatt gatt, int status) {
          connectSuccess = true;
          enableActionWidget(1);
          Log.e(TAG, "onConnectSucc,status:" + status);
          Toast.makeText(MainActivity.this, getString(R.string.connect_success), Toast.LENGTH_SHORT).show();
          handler.sendEmptyMessageDelayed(MSG_REGISTER_LISTENER, 2000);
        }

        @Override public void onBleDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
          Log.e(TAG, "onDisConnected,status:" + status + ",isActiveDisConnected:" + isActiveDisConnected);
          Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
          BleConnection.disconnect();
          actionFlag = false;
          connectSuccess = false;
          enableActionWidget(0);
        }
      });
    } catch (GofunBleException e) {
      e.printStackTrace();
      Log.e(TAG, e.getMessage(), e);
    }
  }

  BLEResponseListener listener = new BLEResponseListener();

  class BLEResponseListener extends OnResponseListener {
    @Override public void onCarCtrlRes(short msgSeq, EventType eventType, byte resultCode) {
      Toast.makeText(MainActivity.this, "控制结果,seq:" + msgSeq + ",action:" + eventType + ",result:" + resultCode,
          Toast.LENGTH_SHORT).show();
    }

    @Override public void onIdCardCtrlRes(short msgSeq, byte resultCode) {
      Toast.makeText(MainActivity.this, "onIdCardCtrlRes 控制结果,seq:" + msgSeq + ",result:" + resultCode, Toast.LENGTH_SHORT)
          .show();
    }

    @Override public void onResponse(Message message) {
      handler.removeMessages(MSG_ACTION_RESPONSE_TIMEOUT);
      super.onResponse(message);
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
      progressDialog.dismiss();
      if (message.getSeq() == BleConnection.getTmpMessage().getSeq()) {
        if (message.getResponseResult() == 0) {
          handler.sendEmptyMessage(MSG_ACTION_RESPONSE_SUCCESS);
        } else {
          handler.sendEmptyMessage(MSG_ACTION_RESPONSE_FAIL);
        }
      }
    }

    @Override public void onCarsysReport(Message message) {
    }
  }
}
