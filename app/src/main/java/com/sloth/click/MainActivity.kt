package com.sloth.click

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import com.sloth.click_util.FastClick
import kotlinx.android.synthetic.main.activity_main.tv_content
import kotlinx.android.synthetic.main.activity_main.tv_title

class MainActivity : AppCompatActivity(), OnClickListener {
  override fun onClick(v: View?) {
    Log.d("MainActivity","点击事件委托者")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    tv_title.setOnClickListener{
      Log.d("MainActivity","点击事件被委托者")
      onClick(it)
    }

    tv_content.setOnClickListener(object : OnClickListener{
      @FastClick
      override fun onClick(v: View?) {
        Log.d("MainActivity","点击内容")
      }

    })
  }

}
