package com.gofun.tbox.tools.ble.app.scanning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.clj.fastble.data.BleDevice;
import com.gofun.tbox.tools.ble.R;
import java.util.ArrayList;
import java.util.List;

public class ScanningAdapter extends RecyclerView.Adapter<ScanningAdapter.ScanningViewHolder> {

  private Context context;
  private List<BleDevice> devices;
  private OnItemClickListener onItemClickListener;

  public ScanningAdapter(Context context) {
    this.context = context;
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
  }

  public void setDevices(List<BleDevice> devices) {
    this.devices = devices;
    notifyDataSetChanged();
  }

  public void addDevice(BleDevice device) {
    if (devices == null) {
      devices = new ArrayList<>();
    }
    devices.add(device);
    notifyDataSetChanged();
  }

  public void removeAllDevice() {
    if (devices != null && !devices.isEmpty()) {
      devices.clear();
      notifyDataSetChanged();
    }
  }

  public BleDevice getItemDevice(int position) {
    return devices == null ? null : devices.get(position);
  }

  @Override public ScanningViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_scanning_device, parent, false);
    ScanningViewHolder viewHolder = new ScanningViewHolder(view);
    return viewHolder;
  }

  @Override public void onBindViewHolder(ScanningViewHolder holder, final int position) {
    if (devices != null) {
      BleDevice bleDevice = devices.get(position);
      holder.deviceName.setText(bleDevice.getName().trim());
      holder.deviceMac.setText(bleDevice.getMac().trim());
      holder.deviceRSSI.setText(String.valueOf(bleDevice.getRssi()));
      holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position);
          }
        }
      });
    }
  }

  @Override public int getItemCount() {
    return devices == null ? 0 : devices.size();
  }

  class ScanningViewHolder extends RecyclerView.ViewHolder {

    TextView deviceName, deviceMac, deviceRSSI;

    public ScanningViewHolder(View itemView) {
      super(itemView);
      deviceName = (TextView) itemView.findViewById(R.id.device_name);
      deviceMac = (TextView) itemView.findViewById(R.id.device_mac);
      deviceRSSI = (TextView) itemView.findViewById(R.id.device_rssi);
    }
  }

  interface OnItemClickListener {
    void onItemClick(int position);
  }
}
