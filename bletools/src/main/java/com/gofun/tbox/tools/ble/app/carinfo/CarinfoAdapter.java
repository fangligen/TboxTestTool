package com.gofun.tbox.tools.ble.app.carinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gofun.tbox.tools.ble.R;

public class CarinfoAdapter extends RecyclerView.Adapter<CarinfoAdapter.CarinfoViewHolder> {
  Context context;
  String[] data, names, units;

  public CarinfoAdapter(Context context) {
    this.context = context;
  }

  public void setData(String[] data, String[] units) {
    this.data = data;
    this.units = units;
    notifyDataSetChanged();
  }

  public void setNames(String[] names) {
    this.names = names;
    notifyDataSetChanged();
  }

  @Override public CarinfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_item_carinfo, parent, false);
    CarinfoViewHolder carinfoViewHolder = new CarinfoViewHolder(view);
    return carinfoViewHolder;
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public void onBindViewHolder(CarinfoViewHolder holder, int position) {
    if (names != null && names.length > 0) {
      holder.tv_title.setText(names[position]);
    }
    if (data != null && data.length > 0) {
      holder.tv_value.setText(data[position]);
    }
    if (units != null && units.length > 0) {
      holder.tv_unit.setText(units[position]);
    }
  }

  @Override public int getItemCount() {
    return names == null ? 0 : names.length;
  }

  class CarinfoViewHolder extends RecyclerView.ViewHolder {
    TextView tv_title, tv_value, tv_unit;

    public CarinfoViewHolder(View itemView) {
      super(itemView);
      tv_title = (TextView) this.itemView.findViewById(R.id.activity_textview_title);
      tv_value = (TextView) this.itemView.findViewById(R.id.activity_textview_value);
      tv_unit = (TextView) this.itemView.findViewById(R.id.activity_textview_unit);
    }
  }
}
