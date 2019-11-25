package com.sloth.click;

import android.view.View;

/**
 * @author: 贺宇成
 * @date: 2019-11-25 13:12
 * @desc:
 */
public class BaseViewHolder {

  private View view;
  BaseViewHolder(View view){
    this.view = view;
  }
  public void setOnClickListener(View.OnClickListener onClickListener){
    view.setOnClickListener(onClickListener);
  }
}
