package com.sloth.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author: 贺宇成*
 * @date: 2019-11-20 17:47
 * @desc:
 */
class SlothClickPlugin implements Plugin<Project> {

  @Override
  void apply(Project project) {
    def appExtension = (AppExtension) project.getProperties().get("android")
    project.extensions.create("slothClickConfig", SlothClickConfig.class)
    def transform = new SlothTransfromV2(project)
    appExtension.registerTransform(transform)

    project.afterEvaluate {
//      SlothClickConfig config = project.slothClickConfig
//      transform.initConfig(project, config)
    }
  }
}