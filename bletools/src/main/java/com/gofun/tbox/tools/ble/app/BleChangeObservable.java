package com.clj.blesample;

import java.util.Observable;

/**
 * Created by gaoqian on 2017/7/11.
 */
public class BleChangeObservable extends Observable {

  private static BleChangeObservable mInstance = null;

  private BleChangeObservable() {

  }

  public static BleChangeObservable getInstance() {
    if (mInstance == null) {
      mInstance = new BleChangeObservable();
    }
    return mInstance;
  }

  @Override public void notifyObservers() {
    setChanged();
    super.notifyObservers();
  }

  @Override public void notifyObservers(Object data) {
    setChanged();
    super.notifyObservers(data);
  }
}
