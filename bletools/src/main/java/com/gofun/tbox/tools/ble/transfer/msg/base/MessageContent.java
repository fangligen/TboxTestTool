package com.gofun.tbox.tools.ble.transfer.msg.base;

import com.gofun.tbox.tools.ble.transfer.util.ByteUtil;

/**
 * 消息内容部分
 *
 * @author dg
 */
public class MessageContent {

  //原始内容
  private byte[] content;

  /**
   * 解码内容部分
   * 在上行消息的内容子类中可以覆盖
   */
  public void decodeContent() {
  }

  /**
   * 编码内容部分
   * 将消息内容组装为二进制字节码
   * 在下行消息的内容子类中可以覆盖
   */
  public byte[] encodeContent() {
    return content;
  }

  /**
   * 打印内容
   */
  public String contentToStr() {
    if (content != null && content.length > 0) {
      if (content.length < 100) {
        return ByteUtil.bytesToHexString(content);
      } else {//如果是升级包的话,太大,不用打印内容
        return "len=" + content.length;
      }
    }
    return "";
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent(byte[] content) {
    this.content = content;
  }
}
