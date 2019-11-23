package com.sloth.click

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import kotlinx.android.synthetic.main.activity_main.tv_title

class MainActivity : AppCompatActivity(), OnClickListener {
  override fun onClick(v: View?) {
    Log.d("MainActivity","点击文字")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    tv_title.setOnClickListener{
      Log.d("MainActivity","setOnClickListener")
      onClick(it)
    }
  }

}
