package com.sloth.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SlothClassVisitor extends ClassVisitor {

  String mClassName

  SlothClassVisitor(ClassVisitor cv) {
    super(Opcodes.ASM5, cv)
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
    if ("onCreate" == name && desc == "(Landroid/os/Bundle;)V") {
      return new SlothOnCreateVisitor(mv, mClassName)
    } else if ("onDestroy" == name && desc == "()V") {
      return new SlothOnDestroyVisitor(mv, mClassName)
    } else if ("onClick" == name && desc == "(Landroid/view/View;)V") {
      logState("$name   $mClassName  $access  $desc $signature $exceptions")
      return new SlothOnClickVisitor(mv,mClassName)
    }
    mv
  }

  @Override
  void visitEnd() {
    super.visitEnd()
  }

  static void logState(String state) {
    println("visit -----------------------------------> $state")
  }
}