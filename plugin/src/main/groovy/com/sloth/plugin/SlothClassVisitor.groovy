package com.sloth.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SlothClassVisitor extends ClassVisitor {

  String mClassName

  boolean enableLife = true

  SlothClassVisitor(ClassVisitor cv,boolean enableLife) {
    super(Opcodes.ASM5, cv)
    this.enableLife = enableLife
  }

  @Override
  void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    this.mClassName = name
    super.visit(version, access, name, signature, superName, interfaces)
  }

  @Override
  MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    def mv = cv.visitMethod(access, name, desc, signature, exceptions)
    if (!SlothTransform.slothClickConfig.enableVisit){
      return mv
    }
    if ("onCreate" == name && desc == "(Landroid/os/Bundle;)V" && this.enableLife) {
      SlothLogHelper.getDefault().appendLine("$mClassName --> $name")
      return new SlothOnCreateVisitor(mv, mClassName)
    } else if ("onDestroy" == name && desc == "()V" && this.enableLife) {
      SlothLogHelper.getDefault().appendLine("$mClassName --> $name")
      return new SlothOnDestroyVisitor(mv, mClassName)
    } else if ("onClick" == name && desc == "(Landroid/view/View;)V") {
      SlothLogHelper.getDefault().appendLine("$mClassName --> $name")
      return new SlothOnClickVisitor(mv,mClassName)
    }
    mv
  }

  @Override
  void visitEnd() {
    super.visitEnd()
  }

}