package com.gofun.tbox.tools.ble.app.control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import com.clj.fastble.data.BleDevice;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.app.BaseActivity;
import com.gofun.tbox.tools.ble.app.carinfo.CarinfoActivity;
import com.gofun.tbox.tools.ble.transfer.Action;

public class ControlActivity extends BaseActivity implements ControlContract.IControlView, View.OnClickListener {

  private ControlContract.IControlPresenter presenter;
  TextView deviceName, deviceMac, deviceStatus;
  TextView successCount, failCount, timeOutCount;
  View editView;
  Button stresssingBtn;
  boolean isStressing;
  private ProgressDialog progressDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_control);
    initGridLayout();
    init();
  }

  private void initGridLayout() {
    GridLayout mGridLayout = (GridLayout) findViewById(R.id.grid_layout);
    int columnCount = mGridLayout.getColumnCount();
    int screenWidth =
        this.getWindowManager().getDefaultDisplay().getWidth() - getResources().getDimensionPixelOffset(R.dimen.padding) * 2;
    for (int i = 0; i < mGridLayout.getChildCount(); i++) {
      LinearLayout button = (LinearLayout) mGridLayout.getChildAt(i);
      button.getLayoutParams().width = screenWidth / columnCount;
      button.getLayoutParams().height = screenWidth / columnCount;
    }
  }

  private void init() {
    Toolbar mToolbarTb = (Toolbar) findViewById(R.id.tb_toolbar);
    setSupportActionBar(mToolbarTb);
    getSupportActionBar().setHomeAsUpIndicator(R.mipmap.navigation_back);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    editView = findViewById(R.id.activity_control_edit);
    presenter = new ControlPresenter(this, this);
    successCount = (TextView) findViewById(R.id.activity_control_success_count);
    failCount = (TextView) findViewById(R.id.activity_control_fail_count);
    timeOutCount = (TextView) findViewById(R.id.activity_control_time_out_count);
    deviceName = (TextView) findViewById(R.id.activity_control_device_name);
    deviceMac = (TextView) findViewById(R.id.activity_control_device_mac);
    deviceStatus = (TextView) findViewById(R.id.activity_control_device_status);
    findViewById(R.id.activity_btn_connect).setOnClickListener(this);
    findViewById(R.id.btn_disconnect).setOnClickListener(this);
    stresssingBtn = (Button) findViewById(R.id.activity_btn_start);
    stresssingBtn.setOnClickListener(this);
    findViewById(R.id.activity_btn_pause).setOnClickListener(this);
    findViewById(R.id.activity_btn_stop).setOnClickListener(this);
    findViewById(R.id.btn_opendoor).setOnClickListener(this);
    findViewById(R.id.btn_closedoor).setOnClickListener(this);
    findViewById(R.id.btn_opendoor_poweron).setOnClickListener(this);
    findViewById(R.id.btn_closedoor_poweroff).setOnClickListener(this);
    findViewById(R.id.btn_poweron).setOnClickListener(this);
    findViewById(R.id.btn_poweroff).setOnClickListener(this);
    findViewById(R.id.activity_control_edit).setOnClickListener(this);
    findViewById(R.id.btn_find_car).setOnClickListener(this);
    mToolbarTb.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (presenter != null) {
          presenter.stopStressing();
        }
        finish();
      }
    });
    BleDevice device = getIntent().getParcelableExtra("device");
    reflashDevice(device);
  }

  private void reflashDevice(BleDevice device) {
    if (device != null) {
      if (!TextUtils.isEmpty(device.getName())) {
        deviceName.setText(device.getName().trim());
      }
      if (!TextUtils.isEmpty(device.getMac())) {
        deviceMac.setText(device.getMac().trim());
      }
    }
  }

  @Override public void finish() {
    super.finish();
    if (presenter != null) {
      presenter.stopStressing();
    }
  }

  @Override public void onDisconnect() {
    Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
    finish();
  }

  @Override public void onCmdSend() {
    showProgressDialog();
  }

  @Override public void onCmdResponse() {
    closeProgressDialog();
  }

  @Override public void onConnectStatus(boolean connected, BleDevice bleDevice) {
    if (connected) {
      reflashDevice(bleDevice);
    }
  }

  @Override public void reflushResult(int success, int fail, int timeout) {
    successCount.setText(String.valueOf(success));
    failCount.setText(String.valueOf(fail));
    timeOutCount.setText(String.valueOf(timeout));
  }

  @Override public void getKey(boolean success) {
    if (success) {
      deviceStatus.setText("校验中");
    } else {
      deviceStatus.setText("Key获取失败");
      presenter.disConnect();
    }
  }

  @Override public void checkKey(boolean success) {
    if (success) {
      deviceStatus.setText("鉴权成功");
    } else {
      deviceStatus.setText("鉴权失败");
      presenter.disConnect();
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.activity_control_edit:
        showPopupMenu(editView);
        break;
      case R.id.activity_btn_connect:
        break;
      case R.id.btn_disconnect:
        presenter.disConnect();
        break;
      case R.id.activity_btn_start:
        if (isStressing) {
          isStressing = false;
          stresssingBtn.setText("压力测试");
          presenter.stopStressing();
        } else {
          isStressing = true;
          stresssingBtn.setText("停止测试");
          presenter.startStressing();
        }
        break;
      case R.id.activity_btn_pause:
        presenter.stopStressing();
        Intent intent = new Intent(this, CarinfoActivity.class);
        startActivity(intent);
        break;
      case R.id.activity_btn_stop:
        break;
      case R.id.btn_opendoor:
        presenter.sendCmd(Action.CarCtrl.OPENDOOR);
        break;
      case R.id.btn_closedoor:
        presenter.sendCmd(Action.CarCtrl.CLOSEDOOR);
        break;
      case R.id.btn_opendoor_poweron:
        presenter.sendCmd(Action.CarCtrl.OPENDOOR_AND_POWERON);
        break;
      case R.id.btn_closedoor_poweroff:
        presenter.sendCmd(Action.CarCtrl.CLOSEDOOR_AND_POWEROFF);
        break;
      case R.id.btn_poweron:
        presenter.sendCmd(Action.CarCtrl.POWERON);
        break;
      case R.id.btn_poweroff:
        presenter.sendCmd(Action.CarCtrl.POWEROFF);
        break;
      case R.id.btn_find_car:
        presenter.sendCmd(Action.CarCtrl.FINDCAR);
        break;
      default:
    }
  }

  private void showProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("发送中...");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.show();
  }

  private void closeProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  private void showPopupMenu(View view) {
    PopupMenu popupMenu = new PopupMenu(this, view);
    popupMenu.setGravity(Gravity.LEFT);
    popupMenu.getMenuInflater().inflate(R.menu.device_option_menu, popupMenu.getMenu());
    popupMenu.show();
    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
          case R.id.menu_disconnect:
            presenter.disConnect();
            finish();
            break;
          case R.id.menu_edit:
            showEditDialog();
            break;
        }
        return true;
      }
    });
    popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
      @Override public void onDismiss(PopupMenu menu) {
      }
    });
  }

  private void showEditDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View view = LayoutInflater.from(this).inflate(R.layout.dialog_device_mac_input, null);
    TextView cancel = (TextView) view.findViewById(R.id.dialog_mac_input_cancel);
    TextView sure = (TextView) view.findViewById(R.id.dialog_mac_input_ok);
    final EditText edittext = (EditText) view.findViewById(R.id.dialog_mac_input_edittext);
    edittext.setHint(deviceMac.getText());
    final Dialog dialog = builder.create();
    dialog.show();
    dialog.getWindow().setContentView(view);
    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    cancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });
    sure.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        presenter.startConnect(edittext.getText().toString().trim());
        dialog.dismiss();
      }
    });
  }
}
