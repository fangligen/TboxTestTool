package com.gofun.tbox.tools.ble.transfer.msg;

import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;

/**
 * 控制命令回复
 * 终端==>手机
 */
public class CarCtrlContentUp extends MessageContent {

	//控制结果
	private byte result;

	@Override
	public void decodeContent() {
		result = getContent()[0];
	}

	@Override
	public String contentToStr() {
		return "content [result=" + result + "]";
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}
}
