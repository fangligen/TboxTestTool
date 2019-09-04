package com.gofun.tbox.tools.ble.app.scanning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.blankj.utilcode.util.ToastUtils;
import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.app.BaseActivity;
import com.gofun.tbox.tools.ble.app.control.ControlActivity;
import java.util.List;

public class ScanningActivity extends BaseActivity
    implements View.OnClickListener, ScanningContract.IScanningView, DialogScanning.OnSelectorListener {

  View scanningBtn;
  ScanningContract.IScanningPresenter iScanningPresenter;
  ImageView scanIcon;
  TextView scanTxt;
  private DialogScanning mDialogSelector;
  private ProgressDialog progressDialog;
  View connectedDeviceLay;
  TextView deviceName, deviceMac, deviceRssi;
  private EditText et_name, et_mac, et_uuid;
  private Switch sw_auto;

  private BleDevice tmpDevice;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_scanning);
    initView();
  }

  private void initView() {
    scanIcon = (ImageView) findViewById(R.id.btn_scan_icon);
    scanTxt = (TextView) findViewById(R.id.btn_scan_txt);
    iScanningPresenter = new ScanningPresenter(this, this);
    scanningBtn = findViewById(R.id.activity_scanning_btn);
    scanningBtn.setOnClickListener(this);
    deviceName = (TextView) findViewById(R.id.device_name);
    deviceMac = (TextView) findViewById(R.id.device_mac);
    deviceRssi = (TextView) findViewById(R.id.device_rssi);
    connectedDeviceLay = findViewById(R.id.activity_scanning_device_lay);
    connectedDeviceLay.setOnClickListener(this);
    et_name = (EditText) findViewById(R.id.et_name);
    et_mac = (EditText) findViewById(R.id.et_mac);
    et_uuid = (EditText) findViewById(R.id.et_uuid);
    sw_auto = (Switch) findViewById(R.id.sw_auto);
  }

  @Override protected void onResume() {
    super.onResume();
    if (BleManager.getInstance().getAllConnectedDevice() != null && !BleManager.getInstance().getAllConnectedDevice().isEmpty()) {
      tmpDevice = BleManager.getInstance().getAllConnectedDevice().get(0);
    } else {
      tmpDevice = null;
      hideConnectedView();
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.activity_scanning_btn:
        try {
          if (!BleManager.getInstance().isSupportBle()) {
            ToastUtils.showLong("设备不支持BLE");
          }
        } catch (Exception e) {
          ToastUtils.showLong("BLE设备出错!");
          return;
        }
        if (checkPermissions()) {
          iScanningPresenter.setScanRule(et_uuid.getText().toString(), et_name.getText().toString(), et_mac.getText().toString(),
              sw_auto.isChecked());
          iScanningPresenter.startScanning();
        }
        break;
      case R.id.activity_scanning_device_lay:
        goNextPage(tmpDevice);
        break;
      default:
    }
  }

  @Override public void onSelectorData(BleDevice bleDevice, int position) {
    iScanningPresenter.startConnect(bleDevice);
  }

  @Override public void cancel() {
    if (iScanningPresenter != null) {
      iScanningPresenter.stopScanning();
      closeSccanningDialog();
    }
  }

  @Override public void startScanning() {
    scanTxt.setText("扫描中..");
    startAnim();
    showScanningDialog();
  }

  @Override public void startConnect() {
    closeSccanningDialog();
    showProgressDialog();
  }

  @Override public void onScanning(BleDevice bleDevice) {
    if (mDialogSelector != null) {
      mDialogSelector.addDevice(bleDevice);
    }
  }

  @Override public void onScanningFinished(List<BleDevice> bleDevices) {
    mDialogSelector.stopAnimal();
    stopAnim();
    scanTxt.setText("开始扫描");
    if (mDialogSelector.getDevices() == 0) {
      closeSccanningDialog();
      Toast.makeText(this, "未找到设备", Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void onConnectSuccess(BleDevice bleDevice) {
    tmpDevice = bleDevice;
    closeSccanningDialog();
    cancelProgressDialog();
    showConnectedView(bleDevice);
    goNextPage(bleDevice);
  }

  @Override public void onConnectFail() {
    closeSccanningDialog();
    cancelProgressDialog();
    hideConnectedView();
    Toast.makeText(this, "连接失败，请重试", Toast.LENGTH_SHORT).show();
  }

  private void startAnim() {
    Animation circle_anim = AnimationUtils.loadAnimation(this, R.anim.anim_round_rotate);
    LinearInterpolator interpolator = new LinearInterpolator();
    circle_anim.setInterpolator(interpolator);
    if (circle_anim != null) {
      scanIcon.startAnimation(circle_anim);
    }
  }

  private void stopAnim() {
    scanIcon.clearAnimation();
  }

  private void showScanningDialog() {
    mDialogSelector = new DialogScanning(this, this);
    mDialogSelector.setCancelable(false);
    mDialogSelector.show();
    mDialogSelector.startAnimal();
  }

  private void closeSccanningDialog() {
    if (mDialogSelector != null) {
      mDialogSelector.dismiss();
    }
  }

  private void showProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("连接中...");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.show();
  }

  private void cancelProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  private void showConnectedView(BleDevice device) {
    connectedDeviceLay.setVisibility(View.VISIBLE);
    deviceName.setText(device.getName().trim());
    deviceMac.setText(device.getMac().trim());
    deviceRssi.setText(String.valueOf(device.getRssi()));
  }

  private void hideConnectedView() {
    connectedDeviceLay.setVisibility(View.GONE);
    deviceMac.setText("");
    deviceName.setText("");
    deviceRssi.setText("");
  }

  private void goNextPage(BleDevice bleDevice) {
    if (bleDevice != null) {
      clearConnectedSetting();
      Intent intent = new Intent(this, ControlActivity.class);
      intent.putExtra("device", bleDevice);
      startActivity(intent);
    }
  }

  private void clearConnectedSetting() {
    et_mac.setText("");
    et_name.setText("");
    et_uuid.setText("");
    sw_auto.setChecked(false);
  }
}
