package com.gofun.tbox.tools.ble.app.account;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import com.gofun.tbox.tools.ble.app.PreferencesUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import static com.gofun.tbox.tools.ble.app.account.LoginContract.Preferences_Login_Name;
import static com.gofun.tbox.tools.ble.app.account.LoginContract.Preferences_Login_Pwd;
import static com.gofun.tbox.tools.ble.app.account.LoginContract.Preferences_Name;

public class LoginPresenter implements LoginContract.ILoginPresenter {

  private LoginContract.ILoginView iLoginView;
  private Context context;

  public LoginPresenter(LoginContract.ILoginView iLoginView, Context context) {
    this.iLoginView = iLoginView;
    this.context = context;
  }

  @Override public void login(String name, String pwd) {
    loginEm(name, pwd);
  }

  private void loginEm(final String name, final String pwd) {
    EMClient.getInstance().login(name, pwd, new EMCallBack() {
      @Override public void onSuccess() {
        Log.d("main", "登录聊天服务器成功！");
        PreferencesUtils.getInstance(context, Preferences_Name).putString(Preferences_Login_Name, name);
        PreferencesUtils.getInstance(context, Preferences_Name).putString(Preferences_Login_Pwd, pwd);
        EMClient.getInstance().logout(false);
        iLoginView.reflushUi(true);
      }

      @Override public void onProgress(int progress, String status) {

      }

      @Override public void onError(int code, String message) {
        Log.d("main", code + "==" + message);

        handler.post(new Runnable() {
          @Override public void run() {
            iLoginView.reflushUi(false);
          }
        });
      }
    });
  }

  Handler handler = new Handler();
}
