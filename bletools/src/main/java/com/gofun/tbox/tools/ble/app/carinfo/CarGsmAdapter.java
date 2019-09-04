package com.gofun.tbox.tools.ble.app.carinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gofun.tbox.tools.ble.R;

public class CarGsmAdapter extends RecyclerView.Adapter<CarGsmAdapter.CarGsmViewHolder> {
  String[] names, datas;
  Context context;

  public CarGsmAdapter(Context context) {
    this.context = context;
  }

  public void setNames(String[] names) {
    this.names = names;
    notifyDataSetChanged();
  }

  public void setDatas(String[] datas) {
    this.datas = datas;
    notifyDataSetChanged();
  }

  @Override public CarGsmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.view_item_gsm, parent, false);
    CarGsmViewHolder carinfoViewHolder = new CarGsmViewHolder(view);
    return carinfoViewHolder;
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public void onBindViewHolder(CarGsmViewHolder holder, int position) {
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

  class CarGsmViewHolder extends RecyclerView.ViewHolder {
    TextView tv_title, tv_value;

    public CarGsmViewHolder(View itemView) {
      super(itemView);
      tv_title = (TextView) this.itemView.findViewById(R.id.activity_textview_gsm_title);
      tv_value = (TextView) this.itemView.findViewById(R.id.activity_textview_gsm_value);
    }
  }
}
