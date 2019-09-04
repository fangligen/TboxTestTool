package com.gofun.tbox.tools.ble.transfer.msg;

import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;

/**
 * 身份证识别下行
 * 手机 -> 车机 0x0403
 * 
 */
public class IdCardReadContentDown extends MessageContent {
	
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
		return "IdCardDistDown [cmd=" + cmd + "]";
	}

	public byte getCmd() {
		return cmd;
	}

	public void setCmd(byte cmd) {
		this.cmd = cmd;
	}
	
}
