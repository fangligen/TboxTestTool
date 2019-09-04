package com.gofun.tbox.tools.ble.app.carinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gofun.tbox.tools.ble.R;

public class CarGpsAdapter extends RecyclerView.Adapter<CarGpsAdapter.CarGpsViewHolder> {
  String[] names, datas;
  Context context;

  public CarGpsAdapter(Context context) {
    this.context = context;
  }

  public void setDatas(String[] datas) {
    this.datas = datas;
    notifyDataSetChanged();
  }

  public void setNames(String[] names) {
    this.names = names;
    notifyDataSetChanged();
  }

  @Override public CarGpsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_item_gps, parent, false);
    CarGpsViewHolder carinfoViewHolder = new CarGpsViewHolder(view);
    return carinfoViewHolder;
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public void onBindViewHolder(CarGpsViewHolder holder, int position) {
    if (names != null && names.length > 0) {
      holder.tv_title.setText(names[position]);
    }
    if (datas != null && datas.length > 0) {
      holder.tv_value.setText(datas[position]);
    }
  }

  @Override public int getItemCount() {
    return names == null ? 0 : names.length;
  }

  class CarGpsViewHolder extends RecyclerView.ViewHolder {
    TextView tv_title, tv_value;

    public CarGpsViewHolder(View itemView) {
      super(itemView);
      tv_title = (TextView) this.itemView.findViewById(R.id.activity_textview_gps_title);
      tv_value = (TextView) this.itemView.findViewById(R.id.activity_textview_gps_value);
    }
  }
}
