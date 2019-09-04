package com.gofun.tbox.tools.ble.app.account;

public interface LoginContract {
  public static final String Preferences_Name = "ble_account";
  public static final String Preferences_Login_Name = "login_name";
  public static final String Preferences_Login_Pwd = "login_pwd";
  interface ILoginView {
    void reflushUi(boolean success);
  }

  interface ILoginPresenter {

    void login(String name, String pwd);
  }
}
