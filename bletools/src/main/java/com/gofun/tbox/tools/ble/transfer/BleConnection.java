package com.gofun.tbox.tools.ble.transfer;

import android.util.Log;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.exception.BleException;
import com.gofun.tbox.tools.ble.transfer.callback.OnConnectionListener;
import com.gofun.tbox.tools.ble.transfer.callback.OnResponseListener;
import com.gofun.tbox.tools.ble.transfer.exception.GofunBleException;
import com.gofun.tbox.tools.ble.transfer.msg.base.Message;
import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;
import com.gofun.tbox.tools.ble.transfer.util.ByteUtil;
import com.gofun.tbox.tools.ble.transfer.util.SequenceUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * 远程控制辅助类
 * 封将请求及响应
 *
 * @author dg
 */
public class BleConnection {
  private static BleConnection instance;
  private static int TIMEOUT = 8;//秒
  private static final String TAG = "gofun_ble_conn";
  //    private OnResponseListener onResponseListener;
  private static boolean inited = false;
  private static Message tmpMessage;
  private static byte tmpResult;
  private static byte contentLocalLen;
  private static byte[] contentLocal;
  private static MessageContent mesContentLocal;
  private static Message recMessage;

  private List<OnResponseListener> listeners = new ArrayList<>();

  public static BleConnection getInstance() {
    if (instance == null) {
      synchronized (BleConnection.class) {
        if (instance == null) {
          instance = new BleConnection();
        }
      }
    }
    return instance;
  }

  private BleConnection() {
    contentLocalLen = 0;
    recMessage = new Message();
    contentLocal = new byte[256];
    mesContentLocal = new MessageContent();
    mesContentLocal.setContent(contentLocal);
    recMessage.setContent(mesContentLocal);
  }

  public void addListener(OnResponseListener onResponseListener) {
    if (!listeners.contains(onResponseListener)) {
      listeners.add(onResponseListener);
    }
  }

  public void removeListener(OnResponseListener listener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener);
    }
  }

  public static Message getTmpMessage() {
    return tmpMessage;
  }

  public static Message getRecMessage() {
    return recMessage;
  }

  /**
   * 初始化方法
   */
  public static void init() {
    inited = true;
  }

  /**
   * 连接车机蓝牙
   *
   * @return 连接成功返回true
   */
  public static void connect(String blemac, OnConnectionListener callback) throws GofunBleException {
    Log.i(TAG, "....connect");
    //如果已经连接不再连
    if (GofunBleContext.getInstance().isConnected()) {
      return;
    }
    BleManager.getInstance().connect(blemac, callback);
  }

  /**
   * 断开连接
   */
  public static void disconnect() {
    Log.i(TAG, "断开连接");
    BleManager.getInstance().disconnect(GofunBleContext.getInstance().getBleDevice());
    GofunBleContext.getInstance().setConnected(false);
    GofunBleContext.getInstance().setBleDevice(null);
    GofunBleContext.getInstance().setBleMac(null);
  }

  /**
   * 向远程终端发送指令
   * 默认10秒超时等待
   *
   * @param downMsg 请求指令消息
   * @throws Exception
   */
  public static void request(Message downMsg, BleWriteCallback callback) throws GofunBleException {
    request(downMsg, TIMEOUT, callback);
  }

  /**
   * 向远程终端发送指令
   *
   * @param downMsg 请求指令消息
   * @param timeOut 超时响应时间
   * @throws Exception
   */
  public static void request(Message downMsg, long timeOut, BleWriteCallback callback) throws GofunBleException {
    if (GofunBleContext.getInstance().getBleDevice() == null) {
      throw new GofunBleException(ResultCode.NOT_CONNECTED);
    }
    downMsg.setSeq(SequenceUtil.getSequence());
    byte[] content = downMsg.getContent().encodeContent();
    downMsg.getContent().setContent(content);
    downMsg.setLen((byte) (content.length + 5));
    Log.i(TAG, "ble请求:" + downMsg);

    BleManager.getInstance()
        .write(GofunBleContext.getInstance().getBleDevice(), GofunBleContext.getServiceUUID(), GofunBleContext.getCharacterUUID(),
            downMsg.getBytes(), callback);
    tmpMessage = downMsg;
    Log.e(TAG, "发送:" + ByteUtil.bytesToHexString(downMsg.getBytes()));
  }

  public void openNotify() {
    BleManager.getInstance()
        .notify(GofunBleContext.getInstance().getBleDevice(), GofunBleContext.getServiceUUID(),
            GofunBleContext.getCharacterUUID(), new BleNotifyCallback() {
              @Override public void onNotifySuccess() {
                Log.e(TAG, "....onNotifySuccess");
              }

              @Override public void onNotifyFailure(BleException exception) {
                Log.e(TAG, "onNotifyFailure:" + exception.getDescription());
              }

              @Override public void onCharacteristicChanged(byte[] data) {
                Log.i(TAG, "接收:" + ByteUtil.bytesToHexString(data));
                if (data.length <= Message.LEN_MIN) {
                  Log.e(TAG, "报文长度不正确,len=" + data.length);
                  return;
                }
                if (data[0] != Message.begin) {
                  Log.e(TAG, "报文头不正确");
                  return;
                }
                if (data[1] != Message.up_mid) {
                  Log.e(TAG, "报文头不正确");
                  return;
                }
                byte lenOfBody = data[2];
                if (lenOfBody < 0 || lenOfBody > 50) {//太大或太小都不正确
                  Log.e(TAG, "报文长度不合法,len=" + lenOfBody);
                  return;
                }
                if (data.length < (lenOfBody - Message.LEN_HEAD)) {
                  Log.e(TAG, "报文长度不合法,len=" + lenOfBody);
                  return;
                }
                byte event = data[5];
                if (!EventType.validCMD(event)) {
                  Log.e(TAG, "不支持的指令,event=" + event);
                  return;
                }

                short seq = ByteUtil.bytes2Short(ByteUtil.subBytes(data, Message.LEN_HEAD, 2));
                if (seq < 0 || seq > Short.MAX_VALUE) {
                  Log.e(TAG, "报文序号不合法,seq=" + seq);
                }
                Message message = new Message();
                if (event == EventType.CMD_RETURNCAR.getEvent()) {
                  if (data[7] == 1) tmpResult = data[8];
                  if (data[6] != data[7]) {
                    Log.e(TAG, "event:" + event + "分包发送，等待下一包");
                    return;
                  }
                  message.setResponseResult(tmpResult);
                } else if (event == EventType.CMD_KEYGET.getEvent()) {
                  message.setResponseResult((byte) 0);
                } else if (event == EventType.CMD_CARSYS_INFO.getEvent()) {
                  message.setSeq(seq);
                  message.setEvent(event);
                  for (OnResponseListener listener : listeners) {
                    listener.onCarsysReport(message);
                  }
                  return;
                } else if (event == EventType.CMD_CARSYS_GPS.getEvent()) {
                  if (data[7] == 1) contentLocalLen = 0;
                  byte len = (byte) (data[2] - (byte) 7);
                  if (len <= 0 || len >= 14) return;
                  for (int i = 0; i < len; i++)
                    contentLocal[contentLocalLen + i] = data[i + 8];
                  contentLocalLen += len;
                  recMessage.setLen(contentLocalLen);
                  if (data[6] != data[7]) {
                    return;
                  } else {
                    recMessage.setEvent(event);
                    for (OnResponseListener listener : listeners) {
                      listener.onCarsysReport(recMessage);
                    }
                    return;
                  }
                } else if (event == EventType.CMD_CARSYS_GSM.getEvent()) {
                  contentLocalLen = (byte) (data[2] - (byte) 5);
                  if (contentLocalLen <= 0 || contentLocalLen >= 14) return;
                  recMessage.setLen(contentLocalLen);
                  recMessage.setEvent(event);
                  for (int i = 0; i < contentLocalLen; i++)
                    contentLocal[i] = data[i + 6];
                  for (OnResponseListener listener : listeners) {
                    listener.onCarsysReport(recMessage);
                  }
                  return;
                } else if (event == EventType.CMD_CARSYS_CAR.getEvent()) {
                  if (data[7] == 1) contentLocalLen = 0;
                  byte len = (byte) (data[2] - (byte) 7);
                  if (len <= 0 || len >= 14) return;
                  for (int i = 0; i < len; i++)
                    contentLocal[contentLocalLen + i] = data[i + 8];
                  contentLocalLen += len;
                  recMessage.setLen(contentLocalLen);
                  if (data[6] != data[7]) {
                    return;
                  } else {
                    recMessage.setEvent(event);
                    for (OnResponseListener listener : listeners) {
                      listener.onCarsysReport(recMessage);
                    }
                    return;
                  }
                } else {
                  message.setResponseResult(data[6]);
                }
                message.setLen(lenOfBody);
                message.setSeq(seq);
                message.setEvent(event);

                byte[] contentB = ByteUtil.subBytes(data, 6, lenOfBody - 5);
                message.setContentB(contentB);
                message.setCrc(ByteUtil.subBytes(data, lenOfBody - 2, 2));
                for (OnResponseListener listener : listeners) {
                  listener.onResponse(message);
                }
              }
            });
  }

  public void openNotify(final OnResponseListener onResponseListener) {
    addListener(onResponseListener);
    openNotify();
  }
}
