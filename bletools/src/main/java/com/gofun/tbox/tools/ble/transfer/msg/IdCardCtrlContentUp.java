package com.gofun.tbox.tools.ble.transfer.msg;

import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;

/**
 * 身份证设备控制上行
 * 车机 -> 手机
 */
public class IdCardCtrlContentUp extends MessageContent {
	
	//控制结果
	private byte result;

	@Override
	public void decodeContent() {
		result = getContent()[0];
	}

	@Override
	public String contentToStr() {
		return "IdCardCtrlContentUp [result=" + result + "]";
	}
	
	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}
	
}
