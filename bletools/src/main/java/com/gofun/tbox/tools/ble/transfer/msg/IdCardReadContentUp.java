package com.gofun.tbox.tools.ble.transfer.msg;

import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;
import com.gofun.tbox.tools.ble.transfer.util.ByteUtil;

/**
 * 身份证认证上行
 * 车机 =>手机
 */
public class IdCardReadContentUp extends MessageContent {

  //读证结果
  private byte result;

  //身份认证模式
  private byte readMode;

  //离线身份认证数据长度
  private byte offlineIdlen;

  //离线身份认证数据
  private String offlineId;

  //在线身份认证数据长度
  private byte onlineIdlen;

  //在线身份认证数据
  private String onlineId;

  @Override public void decodeContent() {
    result = getContent()[0];
    if (result == 0x00) {
      readMode = getContent()[2];
      System.out.println(ByteUtil.bytesToHexString(getContent()));
      offlineIdlen = getContent()[3];
      offlineId = new String(ByteUtil.subBytes(getContent(), 4, offlineIdlen));
      onlineIdlen = getContent()[4 + offlineIdlen];
      onlineId = new String(ByteUtil.subBytes(getContent(), 5 + offlineIdlen, onlineIdlen));
    }
  }

  @Override public String contentToStr() {
    return "IdCardReadContentUp [result="
        + result
        + ", readMode="
        + readMode
        + ", offlineIdlen="
        + offlineIdlen
        + ", offlineId="
        + offlineId
        + ", onlineIdlen="
        + onlineIdlen
        + ", onlineId="
        + onlineId
        + "]";
  }

  public byte getResult() {
    return result;
  }

  public void setResult(byte result) {
    this.result = result;
  }

  public byte getReadMode() {
    return readMode;
  }

  public void setReadMode(byte readMode) {
    this.readMode = readMode;
  }

  public byte getOfflineIdlen() {
    return offlineIdlen;
  }

  public void setOfflineIdlen(byte offlineIdlen) {
    this.offlineIdlen = offlineIdlen;
  }

  public String getOfflineId() {
    return offlineId;
  }

  public void setOfflineId(String offlineId) {
    this.offlineId = offlineId;
  }

  public byte getOnlineIdlen() {
    return onlineIdlen;
  }

  public void setOnlineIdlen(byte onlineIdlen) {
    this.onlineIdlen = onlineIdlen;
  }

  public String getOnlineId() {
    return onlineId;
  }

  public void setOnlineId(String onlineId) {
    this.onlineId = onlineId;
  }
}
