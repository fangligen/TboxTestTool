package com.gofun.tbox.tools.ble.app.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.clj.fastble.BleManager;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.app.BaseActivity;
import com.gofun.tbox.tools.ble.app.PreferencesUtils;
import com.gofun.tbox.tools.ble.app.scanning.ScanningActivity;
import java.util.List;

import static com.gofun.tbox.tools.ble.app.account.LoginContract.Preferences_Login_Name;
import static com.gofun.tbox.tools.ble.app.account.LoginContract.Preferences_Login_Pwd;
import static com.gofun.tbox.tools.ble.app.account.LoginContract.Preferences_Name;

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginContract.ILoginView {

  EditText nameInput, pwdInput;
  LoginContract.ILoginPresenter iLoginPresenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_account);
    init();
  }

  void init() {
    iLoginPresenter = new LoginPresenter(this, this);
    nameInput = (EditText) findViewById(R.id.activity_login_username_edit);
    pwdInput = (EditText) findViewById(R.id.activity_login_pwd_edit);
    findViewById(R.id.activity_login_login_btn).setOnClickListener(this);
    String name = PreferencesUtils.getInstance(this, Preferences_Name).getString(Preferences_Login_Name, "");
    String pwd = PreferencesUtils.getInstance(this, Preferences_Name).getString(Preferences_Login_Pwd, "");
    if (!TextUtils.isEmpty(name)) {
      nameInput.setText(name);
    }
    if (!TextUtils.isEmpty(pwd)) {
      pwdInput.setText(pwd);
    }
    checkPermissions();
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.activity_login_login_btn:
        //if (!PermissionUtils.isGranted(PermissionConstants.LOCATION)) {
        //  showMissingPermissionDialog("请打开位置权限", Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //} else
          if (!checkPermissions()) {
          showMissingPermissionDialog();
        } else {
          login();
        }
        break;
    }
  }

  @Override public void reflushUi(boolean success) {
    closeDialog();
    if (success) {
      launch();
    } else {
      Toast.makeText(this, "登录失败，请重试！", Toast.LENGTH_SHORT).show();
    }
  }

  private void login() {
    String name = nameInput.getText().toString();
    String pwd = pwdInput.getText().toString();
    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
      Toast.makeText(this, "请输入账号密码", Toast.LENGTH_SHORT).show();
    } else {
      showProgressDialog();
      iLoginPresenter.login(name.trim(), pwd.trim());
    }
  }

  private void launch() {
    startActivity(new Intent(this, ScanningActivity.class));
    finish();
  }

  ProgressDialog progressDialog;

  private void showProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("登录中...");
    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progressDialog.show();
  }

  private void closeDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
    }
  }

  private void requestLocationPermission() {
    PermissionUtils.permission(PermissionConstants.LOCATION).callback(new PermissionUtils.FullCallback() {
      @Override public void onGranted(List<String> permissionsGranted) {
        checkOpenBle();
      }

      @Override public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
        Toast.makeText(LoginActivity.this, "权限被拒绝", Toast.LENGTH_SHORT).show();
        checkOpenBle();
      }
    }).request();
  }

  private void checkOpenBle() {
    if (!BleManager.getInstance().isBlueEnable()) {
      showMissingPermissionDialog();
    }
  }
}
