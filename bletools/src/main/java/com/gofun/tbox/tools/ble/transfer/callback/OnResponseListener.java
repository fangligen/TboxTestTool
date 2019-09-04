package com.gofun.tbox.tools.ble.transfer.callback;

import android.util.Log;
import com.gofun.tbox.tools.ble.transfer.EventType;
import com.gofun.tbox.tools.ble.transfer.msg.CarCtrlContentUp;
import com.gofun.tbox.tools.ble.transfer.msg.IdCardCtrlContentUp;
import com.gofun.tbox.tools.ble.transfer.msg.IdCardReadContentUp;
import com.gofun.tbox.tools.ble.transfer.msg.base.Message;
import com.gofun.tbox.tools.ble.transfer.msg.base.MessageContent;

/**
 * 蓝牙响应监听器
 * Created by dg on 2018/1/13.
 */

public abstract class OnResponseListener {

    private static String TAG = "gofun_ble";

    /**
     * 当蓝牙有响应时
     * @param message
     */
    public void onResponse(Message message){
        MessageContent content = null;
        if(message.getEvent() == EventType.CMD_CARCTROL_UP.getEvent()){
            content = new CarCtrlContentUp();
        }else if(message.getEvent() == EventType.CMD_IDCARDCTRL.getEvent()){
            content = new IdCardCtrlContentUp();
        }else if(message.getEvent() == EventType.CMD_IDREAD.getEvent()){
            content = new IdCardReadContentUp();
        }
        if(content != null) {
            content.setContent(message.getContentB());
            content.decodeContent();
            message.setContent(content);
        }
        Log.i(TAG,"接收:" + message);
        if(message.getEvent() == EventType.CMD_CARCTROL_UP.getEvent()){
            onCarCtrlRes(message.getSeq(),EventType.getEventType(message.getEvent()),((CarCtrlContentUp)content).getResult());
        }else if(message.getEvent() == EventType.CMD_IDCARDCTRL.getEvent()){
            onIdCardCtrlRes(message.getSeq(),((IdCardCtrlContentUp)content).getResult());
        }else if(message.getEvent() == EventType.CMD_IDREAD.getEvent()){
        }

    }

    /**
     * 控制车机指令响应
     * @param eventType
     * @param resultCode
     */
    public abstract void onCarCtrlRes(short msgSeq,EventType eventType,byte resultCode);

    /**
     * 身份证设备控制指令响应
     * @param resultCode
     */
    public abstract void onIdCardCtrlRes(short msgSeq,byte resultCode);


    public abstract void onCarsysReport(Message message);


}
