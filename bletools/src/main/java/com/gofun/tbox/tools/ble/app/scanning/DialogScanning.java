package com.gofun.tbox.tools.ble.app.scanning;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.clj.fastble.data.BleDevice;
import com.gofun.tbox.tools.ble.R;
import java.util.ArrayList;
import java.util.List;

public class DialogScanning extends Dialog implements ScanningAdapter.OnItemClickListener {
  private List<BleDevice> DialogMemberBean = new ArrayList<>();
  private Context context;
  private OnSelectorListener cdListener;
  private TextView btn_cancel, titleTxt;
  private ImageView iconImg;
  RecyclerView deviceListView;
  ScanningAdapter scanningAdapter;

  public DialogScanning(Context context, OnSelectorListener cdListener) {
    super(context);
    this.context = context;
    this.cdListener = cdListener;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    this.setContentView(R.layout.dialog_scanning);
    this.setCanceledOnTouchOutside(false);
    InitViews();
  }

  private void InitViews() {
    titleTxt = (TextView) findViewById(R.id.dialog_title);
    iconImg = (ImageView) findViewById(R.id.dialog_scan_icon);
    btn_cancel = (TextView) findViewById(R.id.scaning_cancel);
    deviceListView = (RecyclerView) findViewById(R.id.scanning_device_recycler_view);
    scanningAdapter = new ScanningAdapter(context);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    deviceListView.setLayoutManager(linearLayoutManager);
    deviceListView.setAdapter(scanningAdapter);
    scanningAdapter.setOnItemClickListener(this);
    btn_cancel.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        cdListener.cancel();
        cancel();
      }
    });
  }

  /**
   * adpter里面的checkbox监听接口
   *
   * @param position item的位置
   * 改变元数据集的内容
   */
  @Override public void onItemClick(int position) {
    if (cdListener != null) {
      cdListener.onSelectorData(scanningAdapter.getItemDevice(position), position);
    }
  }

  public void addDevice(BleDevice bleDevice) {
    scanningAdapter.addDevice(bleDevice);
    scanningAdapter.notifyDataSetChanged();
  }

  public void startAnimal() {
    Animation circle_anim = AnimationUtils.loadAnimation(context, R.anim.anim_round_rotate);
    LinearInterpolator interpolator = new LinearInterpolator();
    circle_anim.setInterpolator(interpolator);
    if (circle_anim != null) {
      iconImg.startAnimation(circle_anim);
    }
    iconImg.setVisibility(View.VISIBLE);
    titleTxt.setText("扫描中...");
  }

  public int getDevices() {
    return scanningAdapter == null ? 0 : scanningAdapter.getItemCount();
  }

  public void stopAnimal() {
    if (iconImg != null) {
      iconImg.clearAnimation();
    }
    iconImg.setVisibility(View.GONE);
    titleTxt.setText("请选择设备");
  }

  /**
   * 确定 和 取消控件的回调接口
   */
  public interface OnSelectorListener {

    void onSelectorData(BleDevice bleDevice, int position);

    void cancel();
  }
}
