package com.gofun.tbox.tools.ble.transfer.msg;

import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;

/**
 * 身份证设备控制操作下行 
 * 服务端 -> 车机
 * 
 * @author dg
 *
 */
public class IdCardCtrlContentDown extends MessageContent {

	/**
	 * 设置命令 0待机 1联网请求 2复位 3设定读卡器待机 4设定读卡器寻卡
	 */
	private byte cmd;

	@Override
	public byte[] encodeContent() {
		return new byte[]{cmd};
	}

	@Override
	public String contentToStr() {
		return "IdCardCtrlDown [cmd=" + cmd + "]";
	}

	public byte getCmd() {
		return cmd;
	}

	public void setCmd(byte cmd) {
		this.cmd = cmd;
	}
	
}
