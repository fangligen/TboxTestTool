package com.gofun.tbox.tools.ble.app.net;

import java.security.PublicKey;

public class HttpConstant {

  //===================url===========================
  public static final String BASE_URL = "http://osapi159.gftest.cloudns.asia:88/";
  public static final String LOGIN_URL = BASE_URL + "os/login";
  public static final String REGISTER_URL = BASE_URL + "os/register";
  public static final String ORDER_URL = BASE_URL + "order/query";
  public static final String OTA_URL = BASE_URL + "os/upgrade";
  public static final String PARKING_QUERY_URL = BASE_URL + "parking/list";
  public static final String UPDATE_CHECK_URL = BASE_URL + "";
  public static final String LOG_UPLOAD_URL = BASE_URL + "upload/log";
  public static final String DIRVING_UPLOAD_URL = BASE_URL + "upload/driving";
  public static final String FILE_UPLOAD_URL = BASE_URL + "";
  public static final String VIDEO_UPLOAD_URL = BASE_URL + "upload/collide";
  public static final String CLENNESS_UPLOAD_URL = BASE_URL + "upload/cleaness";
  public static final String FILE_UPLOAD_SERVICE = BASE_URL + "upload/fileUpload";

  //=====================end===========================

  //==================common parameter===========
  /**
   * 通用参数：
   *
   * deviceId     设备唯一编号
   *
   * appVersion       软件版本
   *
   * osVersion         操作系统版本
   *
   * timestamp        时间戳
   */
  public static final String PARAMETER_DEVICE_ID = "deviceId";
  public static final String PARAMETER_APP_VERSION = "appVersion";
  public static final String PARAMETER_OS_VERSION = "osVersion";
  public static final String PARAMETER_TIMESTAMP = "timestamp";

  public static final String HEADER_APP_KEY = "AppKey";
  public static final String HEADER_NONCE = "Nonce";
  public static final String HEADER_CURTIME = "CurTime";
  public static final String HEADER_CHECK_SUM = "CheckSum";
  //======================end=========================

  //=====================CODE=========================
  public static final int CODE_OK = 200;
  public static final String DESC_UNKNOW = "未知错误";
  //======================END=========================
}
