package com.gofun.tbox.tools.ble.app;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.blankj.utilcode.util.AppUtils;
import com.clj.fastble.BleManager;
import com.gofun.tbox.tools.ble.R;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
  private static final int REQUEST_CODE_OPEN_GPS = 1;
  private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;

  public static final int PERMISSIONS_GRANTED = 0; // 权限授权
  public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

  private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
  private static final String EXTRA_PERMISSIONS = "me.chunyu.clwang.permission.extra_permission"; // 权限参数
  private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(R.style.AppTheme);
    initTheme();
  }

  private void initTheme() {
    Window window = getWindow();
    window.clearFlags(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    window.getDecorView()
        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(getResources().getColor(R.color.main_bg));
      window.setNavigationBarColor(Color.TRANSPARENT);
    }

    checkPermissions();
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

  // 显示缺失权限提示
  protected void showMissingPermissionDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
    builder.setTitle("提示");
    builder.setMessage("请打开蓝牙");

    // 拒绝, 退出应用
    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        setResult(PERMISSIONS_DENIED);
      }
    });

    builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        startAppSettings();
      }
    });

    builder.show();
  }

  protected void showMissingPermissionDialog(String message, String action) {
    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
    builder.setTitle("提示");
    builder.setMessage(message);

    // 拒绝, 退出应用
    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        setResult(PERMISSIONS_DENIED);
      }
    });

    builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        //Intent intent = new Intent(action);
        //startActivity(intent);
        AppUtils.launchAppDetailsSettings();
      }
    });

    builder.show();
  }

  // 启动应用的设置
  private void startAppSettings() {
    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
    startActivity(intent);
  }

  // 启动位置设置
  private void startLocationSettings() {
    Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
    startActivity(intent);
  }

  protected boolean checkPermissions() {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
      showMissingPermissionDialog();
      return false;
    }

    String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET };
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
    return true;
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
        }
        break;
    }
  }

  private boolean checkGPSIsOpen() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    if (locationManager == null) {
      return false;
    }
    return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_OPEN_GPS) {
      if (checkGPSIsOpen()) {

      }
    }
  }
}
