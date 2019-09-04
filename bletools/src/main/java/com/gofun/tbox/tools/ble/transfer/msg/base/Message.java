package com.gofun.tbox.tools.ble.transfer.msg.base;

import com.gofun.tbox.tools.ble.transfer.util.ByteUtil;
import com.gofun.tbox.tools.ble.transfer.util.CRC16;
import java.io.Serializable;

/**
 *
 * 通讯消息基础类 消息格式
 * +-------------------------------------------------------------+
 * | 起始字节(0x5B12 | 消息体长度1bytes |消息体该段即完整的指令或应答数据 |
 * +-------------------------------------------------------------+
 * @author dg
 */
public class Message implements Serializable{

    private static final long serialVersionUID = 921L;

    /**
     * 起始字节
     */
    public static final byte begin = 0x5B;

    //消息标识
    public static final byte down_mid = (byte)0x1F;

    //消息标识
    public static final byte up_mid = (byte)0xF1;

    /**
     * 消息头字节长度
     */
    public static final short LEN_HEAD = 3;

    /**
     * 消息最小长度(含包体)
     */
    public static final short LEN_MIN = 5;

    // 消息体长度 1bytes
    private byte len;

    // 报文序列号
    private short seq;

    // 命令标识
    private byte event;

    // 回复结果
    private byte responseResult;

    // 校验码
    private byte[] crc;

    //内容部分 字节码
    private byte[] contentB;

    private MessageContent content;

    public Message() {
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public void setResponseResult(byte result) {
        this.responseResult = result;
    }

    public byte getResponseResult() {
        return responseResult;
    }

    public byte getEvent() {
        return event;
    }

    public void setEvent(byte event) {
        this.event = event;
    }

    public byte[] getCrc() {
        crc = new byte[2];
        byte[] contentB = new byte[0];
        if(content != null) {
            contentB = content.getContent();
        }
        //byte[] concat = ByteUtil.concat(ByteUtil.short2Byte(seq), new byte[]{event},contentB );
        byte[] concat = ByteUtil.concat(new byte[]{begin,down_mid,this.getLen()},ByteUtil.short2Byte(seq),new byte[]{event},this.getContent().getContent());
        CRC16.get_crc16(concat, concat.length, crc);
        return crc;
    }

    public void setCrc(byte[] crc) {
        this.crc = crc;
    }

    public byte getLen() {
        return len;
    }

    public void setLen(byte len) {
        this.len = len;
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public byte[] getContentB() {
        return contentB;
    }

    public void setContentB(byte[] contentB) {
        this.contentB = contentB;
    }

    /**
     *
     * @return
     */
    public byte[] getBytes(){
        return ByteUtil.concat(new byte[]{begin,down_mid,this.getLen()},ByteUtil.short2Byte(seq),new byte[]{event},this.getContent().getContent(),this.getCrc());
    }

    @Override
    public String toString() {
        return "Message [len=" + len + ", seq=" + seq
                + ", event=" + Integer.toHexString(event) + ", content={" + (content == null ? "":content.contentToStr()) + "}]";
    }

}
