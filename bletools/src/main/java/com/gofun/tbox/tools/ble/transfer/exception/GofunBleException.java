package com.gofun.tbox.tools.ble.transfer.exception;

import com.gofun.tbox.tools.ble.transfer.ResultCode;

/**
 * BLE通信异常
 * 
 * @author dg
 *
 */
public class GofunBleException extends Exception {

	private static final long serialVersionUID = 11111L;
	private int code;

	public GofunBleException(ResultCode code, String message) {
		super(message);
		this.code = code.getCode();
	}
	
	public GofunBleException(ResultCode code) {
		super(code.getMessage());
		this.code = code.getCode();
	}

	public GofunBleException(String message, Throwable t) {
		super(message, t);
	}

	public GofunBleException(int code, String message, Throwable t) {
		super(message, t);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
