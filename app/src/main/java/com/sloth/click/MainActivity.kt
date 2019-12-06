package com.sloth.click

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import kotlinx.android.synthetic.main.activity_main.tv_content
import kotlinx.android.synthetic.main.activity_main.tv_title

open class MainActivity : BActivity(), OnClickListener {
  override fun onClick(v: View?) {
    Log.d("MainActivity","点击事件委托者")
  }


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    tv_title.setOnClickListener{
      onClick(it)
      Log.d("MainActivity","点击事件被委托者")
    }

    tv_content.setOnClickListener { Log.d("MainActivity","1222111") }
  }


}
