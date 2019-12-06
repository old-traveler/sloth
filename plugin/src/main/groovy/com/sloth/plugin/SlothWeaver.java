package com.sloth.plugin;

import com.sloth.plugin.hunt.BaseWeaver;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * @author: 贺宇成
 * @date: 2019-12-04 17:50
 * @desc:
 */
public class SlothWeaver extends BaseWeaver {

  @Override
  public boolean isWeavableClass(String fullQualifiedClassName) {
    return super.isWeavableClass(fullQualifiedClassName);
  }

  @Override
  protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
    return new SlothClassVisitor(classWriter,true);
  }
}
