package com.gofun.tbox.tools.ble.transfer;

/**
 * 控件动作 1 鸣笛、2 开门、3 关门、4 断电、5 供电,6 供断电恢复默认
 *
 * 2016年2月3日
 */
public class Action {

  /**
   * 将cmd转为CarCtrl
   */
  public static CarCtrl getCarCtrl(int cmd) {
    for (CarCtrl obj : CarCtrl.values()) {
      if (obj.getCmd() == cmd) {
        return obj;
      }
    }
    return null;
  }

  /**
   * 车机管理控制接口指令码
   *
   * @author dg
   */
  public enum CarCtrl {
    FINDCAR((short) 0x01),// 开门
    OPENDOOR((short) 0x02),// 开门
    CLOSEDOOR((short) 0x03),// 锁车
    POWEROFF((short) 0x04),// 断电
    POWERON((short) 0x05),// 供电
    FORCE_OPENDOOR((short) 0x08),  //强制开门
    FORCE_CLOSEDOOR((short) 0x09), // 强制关门
    REVERSAL_POWERON((short) 0x010),// 反转供电
    REVERSAL_AGAIN((short) 0x011),// 再次反转
    FINDCAR_NIGHT((short) 0x0C),// 夜间寻车
    OPENDOOR_AND_POWERON((short) 0x025),// 再次反转
    CLOSEDOOR_AND_POWEROFF((short) 0x034);// 夜间寻车
    short cmd;

    CarCtrl(short cmd) {
      this.cmd = cmd;
    }

    public short getCmd() {
      return cmd;
    }

    /**
     * 是否为支持指令
     */
    public static boolean isValidCmd(short cmd) {
      for (CarCtrl obj : CarCtrl.values()) {
        if (obj.getCmd() == cmd) {
          return true;
        }
      }
      return false;
    }

  }

  /**
   * 指令来源
   *
   * @author dg
   */
  public enum From {
    crm, check, netty, api, job;
  }

  /**
   * 身份证控制相关操作
   * 0待机
   * 1联网请求
   * 2复位
   * 3设定读卡器待机
   * 4设定读卡器寻卡
   *
   * @author dg
   */
  public enum IdCard {
    STANDBY((short) 0x00), CONNECT((short) 0x01), RESET((short) 0x02);

    short cmd;

    IdCard(short cmd) {
      this.cmd = cmd;
    }

    public short getCmd() {
      return cmd;
    }

  }
}
