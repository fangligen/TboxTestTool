package com.gofun.tbox.tools.ble.app.carinfo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.gofun.tbox.tools.ble.R;
import com.gofun.tbox.tools.ble.app.BaseActivity;
import com.gofun.tbox.tools.ble.app.entity.CarInfoStatus;
import com.gofun.tbox.tools.ble.app.wiget.DividerGridItemDecoration;
import com.gofun.tbox.tools.ble.transfer.Request;
import java.util.ArrayList;
import java.util.List;

public class CarinfoActivity extends BaseActivity implements View.OnClickListener, CarinfoContract.ICarInfoView {
  private static final String TAG = "gofun_ble_carinfo";

  private ProgressDialog progressDialog;

  private Button btn_carsys_start = null, btn_carsys_stop = null;

  RecyclerView carinfoRecyler;
  CarinfoAdapter carinfoAdapter;
  static GridLayoutManager carinfoGridLayoutManager;

  RecyclerView carinfoStatusRecyler;
  CarinfoStatusAdapter carinfoStatusAdapter;
  GridLayoutManager carinfoStatusGridLayoutManager;
  List<CarInfoStatus> carInfoStatuss = new ArrayList<>();

  RecyclerView carGpsRecyler;
  CarGpsAdapter carGpsAdapter;
  GridLayoutManager carGpsGridLayoutManager;

  RecyclerView carGsmRecyler;
  CarGsmAdapter carGsmAdapter;
  GridLayoutManager carGsmGridLayoutManager;

  TextView carCount, gpsCount, gmsCount;

  private CarinfoContract.ICarInfoPresenter presenter;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_carinfo);
    progressDialog = new ProgressDialog(this);
    progressDialog.setCancelable(false);
    progressDialog.setTitle("request...");
    init();
    initCarsysTest();
  }

  private void initCarsysTest() {
    presenter = new CarInfoPresenter(this, this);

    carCount = (TextView) findViewById(R.id.result_car);
    gpsCount = (TextView) findViewById(R.id.result_gps);
    gmsCount = (TextView) findViewById(R.id.result_gsm);

    this.btn_carsys_start = (Button) this.findViewById(R.id.activity_carinfo_btn_start);
    btn_carsys_start.setOnClickListener(this);

    this.btn_carsys_stop = (Button) this.findViewById(R.id.activity_carinfo_btn_stop);
    btn_carsys_stop.setOnClickListener(this);
    btn_carsys_start.setEnabled(true);
    btn_carsys_stop.setEnabled(false);

    Toolbar mToolbarTb = (Toolbar) findViewById(R.id.tb_toolbar);
    setSupportActionBar(mToolbarTb);
    getSupportActionBar().setHomeAsUpIndicator(R.mipmap.navigation_back);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    mToolbarTb.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        finish();
      }
    });
  }

  private void init() {
    carinfoRecyler = (RecyclerView) findViewById(R.id.activity_carinfo_recyler_view);
    carinfoAdapter = new CarinfoAdapter(this);
    carinfoAdapter.setNames(getResources().getStringArray(R.array.data_name));
    carinfoGridLayoutManager = new GridLayoutManager(this, 2);
    carinfoRecyler.setLayoutManager(carinfoGridLayoutManager);
    carinfoRecyler.setAdapter(carinfoAdapter);
    carinfoRecyler.addItemDecoration(new DividerGridItemDecoration(this));
    carinfoStatusRecyler = (RecyclerView) findViewById(R.id.activity_carinfoStatus_recyler_view);
    carinfoStatusAdapter = new CarinfoStatusAdapter(this);
    carinfoStatusAdapter.setNames(getResources().getStringArray(R.array.car_status_name));
    carinfoStatusGridLayoutManager = new GridLayoutManager(this, 5);
    carinfoStatusRecyler.setLayoutManager(carinfoStatusGridLayoutManager);
    carinfoStatusRecyler.setAdapter(carinfoStatusAdapter);
    carinfoStatusRecyler.addItemDecoration(new DividerGridItemDecoration(this));

    carGpsRecyler = (RecyclerView) findViewById(R.id.activity_gps_recyler_view);
    carGpsAdapter = new CarGpsAdapter(this);
    carGpsAdapter.setNames(getResources().getStringArray(R.array.gps_name));
    carGpsGridLayoutManager = new GridLayoutManager(this, 2);
    carGpsRecyler.setLayoutManager(carGpsGridLayoutManager);
    carGpsRecyler.setAdapter(carGpsAdapter);
    carGpsRecyler.addItemDecoration(new DividerGridItemDecoration(this));

    carGsmRecyler = (RecyclerView) findViewById(R.id.activity_gsm_recyler_view);
    carGsmAdapter = new CarGsmAdapter(this);
    carGsmAdapter.setNames(getResources().getStringArray(R.array.gsm_name));
    carGsmGridLayoutManager = new GridLayoutManager(this, 2);
    carGsmRecyler.setLayoutManager(carGsmGridLayoutManager);
    carGsmRecyler.setAdapter(carGsmAdapter);
    carGsmRecyler.addItemDecoration(new DividerGridItemDecoration(this));
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.activity_carinfo_btn_start:
        btn_carsys_stop.setEnabled(true);
        btn_carsys_start.setEnabled(false);
        presenter.sendBleCmd((byte) Request.BLE_CMD_DOWN_CARSYS_START);
        break;
      case R.id.activity_carinfo_btn_stop:
        btn_carsys_start.setEnabled(true);
        btn_carsys_stop.setEnabled(false);
        presenter.sendBleCmd((byte) Request.BLE_CMD_DOWN_CARSYS_STOP);
        break;
      default:
    }
  }

  @Override public void onDisconnect() {
    Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
    finish();
  }

  @Override public void finish() {
    super.finish();
    if (presenter != null) {
      presenter.sendBleCmd((byte) Request.BLE_CMD_DOWN_CARSYS_STOP);
    }
  }

  @Override public void reflashResultCount(int car, int gps, int gms) {
    carCount.setText(String.valueOf(car));
    gpsCount.setText(String.valueOf(gps));
    gmsCount.setText(String.valueOf(gms));
  }

  @Override public void reflashCarInfo(String[] data, String[] unit) {
    carinfoAdapter.setData(data, unit);
  }

  @Override public void reflashCarstatus(String[] data) {
    carinfoStatusAdapter.setStatus(data);
  }

  @Override public void reflashCarGsm(String[] data) {
    carGsmAdapter.setDatas(data);
  }

  @Override public void reflashCarGps(String[] data) {
    carGpsAdapter.setDatas(data);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
