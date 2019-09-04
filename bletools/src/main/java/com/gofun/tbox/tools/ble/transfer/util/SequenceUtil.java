package com.gofun.tbox.tools.ble.transfer.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取消息序号
 * @author dg
 *
 */
public class SequenceUtil {
	private static AtomicInteger atomicInteger = new AtomicInteger(100);
	
	public synchronized static short getSequence() {
		int seq = atomicInteger.getAndIncrement();
		if (seq >= Short.MAX_VALUE) {
			atomicInteger.set(100);
		}
		return (short)seq;
	}
	
}