package com.sloth.plugin

class SlothClickConfig {
  boolean enableLife = true
  List<Closure<Boolean>> mRules = new ArrayList<>()

  void addRules(Closure<Boolean> closure){
    mRules.add(closure)
  }

  boolean enableVisit = true

  boolean enableLog = true

  String logFilePath = null

  String clickHelperName = "com/hyc/helper/helper/ClickHelper"


}