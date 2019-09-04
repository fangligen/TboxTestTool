package com.gofun.tbox.tools.ble.transfer;

/**
 * 消息类型 
 */
public enum EventType {

	/**
	 * 车机控制指令
	 * 下行
	 */
	CMD_CARCTROL((byte)0x31),

	CMD_KEYGET((byte)0x21),

	CMD_KEYCHECK((byte)0x22),

	CMD_RETURNCAR((byte)0x11),

	CMD_CARSYS_INFO((byte)0x41),

	CMD_CARSYS_GPS((byte)0x42),

	CMD_CARSYS_GSM((byte)0x43),

	CMD_CARSYS_CAR((byte)0x44),

	/**
	 * 车机控制指令
	 * 下行
	 */
	CMD_CARCTROL_UP((byte)0x71),

	// 身份证设备控制下行指令
	CMD_IDCARDCTRL((byte) 0x04),

	/**
	 * 身份证识别控制指令
	 */
	CMD_IDREAD((byte) 0x05);

	private byte event;

	EventType(byte event) {
		this.event = event;
	}

	/**
	 * 验证是否是允许的指令
	 * @param event
	 * @return
	 */
	public static boolean validCMD(byte event) {
		for (EventType obj : EventType.values()) {
			if (obj.getEvent() == event) {
				return true;
			}
		}
		return false;
	}

	public static EventType getEventType(byte event) {
		for (EventType obj : EventType.values()) {
			if (obj.getEvent() == event) {
				return obj;
			}
		}
		return null;
	}

	public byte getEvent() {
		return event;
	}

	public void setEvent(byte event) {
		this.event = event;
	}

}
