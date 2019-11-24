package com.sloth.plugin

class SlothLogHelper{

  static SlothLogHelper slothLogHelper

  def content = new StringBuilder()
  private String filePath = null

  static SlothLogHelper getDefault(){
    if (slothLogHelper == null){
      synchronized (SlothLogHelper.class){
        if (slothLogHelper == null){
          slothLogHelper = new SlothLogHelper()
        }
      }
    }
    slothLogHelper
  }

  void init(String path){
    filePath = path
  }

  void appendLine(String log){
    content.append(log + "\n")
  }

  void save(){
    if (filePath == null || filePath.length() == 0){
      return
    }
    def file = new File(filePath)
    if (file.exists()){
      file.delete()
    }
    if (file.createNewFile()){
      FileOutputStream fos = null
      try {
        fos = new FileOutputStream(file)
        fos.write(content.toString().getBytes())
      }catch(Exception e){
        e.printStackTrace()
      }finally {
        if (fos != null){
          fos.close()
        }
      }
    }
  }



}