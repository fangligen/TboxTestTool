package com.gofun.tbox.tools.ble.transfer;

/**
 * 错误码
 * Created by dg on 2018/1/8.
 */

public enum ResultCode {
    OK(200, "OK"),
    CONNECT_FAIL(1000, "连接失败"),
    MIN_VERION_ERR(1001, "车机版本低,不支持此命令"),
    MSGLEN_ERR(1002, "帧长度错误"),
    INVALID_PARAM(1003, "参数不正确"),
    TIMEOUT(1004,"车机命令发送超时或未响应"),
    NOT_CONNECTED(1005,"未连接"),
    FAIL(1006,"执行失败"),
    BUSY(1007,"终端总线繁忙"),
    DElAYEXEC(1008,"令命已下发，将在车辆熄火后执行"),
    NOCUTOFF(1009,"请在车辆熄火后执行"),
    IDCARD_ERR(1010,"读证失败"),
    UNKNOWN(-1,"未知错误");

    private int code;

    private String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
