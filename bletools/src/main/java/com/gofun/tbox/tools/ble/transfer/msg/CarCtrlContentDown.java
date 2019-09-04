package com.gofun.tbox.tools.ble.transfer.msg;

import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;
import com.gofun.tbox.tools.ble.transfer.util.ByteUtil;

/**
 * 车辆控制指令同步下行 0x0A01
 * 服务端==>终端0x0A01
 */
public class CarCtrlContentDown extends MessageContent {
	
	/**
	 * 控制指令
	 * 2开门02
	 * 3锁门03
	 * 4断电04
	 * 5供电05
	 */
	private short cmd;

	@Override
	public byte[] encodeContent() {
		return ByteUtil.short2Byte(cmd);
	}

	@Override
	public String contentToStr() {
		return "CarCtrlContentDown [cmd=" + cmd + "]";
	}

	public short getCmd() {
		return cmd;
	}

	public void setCmd(short cmd) {
		this.cmd = cmd;
	}
	
}
