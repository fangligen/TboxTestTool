package com.gofun.tbox.tools.ble.app.carinfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.app.entity.CarInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class CarinfoStatusAdapter extends RecyclerView.Adapter<CarinfoStatusAdapter.CarinfoStatusViewHolder> {
  List<CarInfoStatus> carInfos = new ArrayList<>();
  Context context;

  String[] status, names;

  public CarinfoStatusAdapter(Context context) {
    this.context = context;
  }

  //public void setCarInfos(List<CarInfoStatus> carInfos) {
  //  this.carInfos = carInfos;
  //  notifyDataSetChanged();
  //}

  public void setStatus(String[] status) {
    this.status = status;
    notifyDataSetChanged();
  }

  public void setNames(String[] names) {
    this.names = names;
    notifyDataSetChanged();
  }

  @Override public CarinfoStatusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_item_carinfo_status, parent, false);
    CarinfoStatusViewHolder carinfoViewHolder = new CarinfoStatusViewHolder(view);
    return carinfoViewHolder;
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public void onBindViewHolder(CarinfoStatusViewHolder holder, int position) {
    if (names != null && names.length > 0) {
      holder.tv_title.setText(names[position]);
    }
    if (status != null && status.length > 0) {
      boolean value = Boolean.valueOf(status[position]);
      if (value) {
        holder.itemView.setBackgroundResource(R.color.car_status_true);
      } else {
        holder.itemView.setBackgroundResource(R.color.car_status_false);
      }
      holder.tv_value.setVisibility(View.GONE);
    }
    holder.tv_value.setTextColor(Color.GREEN);
  }

  @Override public int getItemCount() {
    return names == null ? 0 : names.length;
  }

  class CarinfoStatusViewHolder extends RecyclerView.ViewHolder {
    TextView tv_title, tv_value;

    public CarinfoStatusViewHolder(View itemView) {
      super(itemView);
      tv_title = (TextView) this.itemView.findViewById(R.id.activity_textview_status_title);
      tv_value = (TextView) this.itemView.findViewById(R.id.activity_textview_status_value);
    }
  }
}
