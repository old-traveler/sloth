package com.sloth.plugin;

import com.sloth.plugin.hunt.HunterTransform;
import org.gradle.api.Project;

/**
 * @author: 贺宇成
 * @date: 2019-12-04 17:49
 * @desc:
 */
public class SlothTransfromV2 extends HunterTransform {

  public SlothTransfromV2(Project project) {
    super(project);
    this.bytecodeWeaver = new SlothWeaver();
  }


}
