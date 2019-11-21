package com.sloth.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SlothClassVisitor extends ClassVisitor {

  def mClassName

  SlothClassVisitor(ClassVisitor cv) {
    super(Opcodes.ASM5, cv)
    println("SlothClassVisitor ----->")
  }

  @Override
  void visit(int version, int access, String name, String signature, String superName,
      String[] interfaces) {
    this.mClassName = name
    logState("start")
    super.visit(version, access, name, signature, superName, interfaces)
  }

  @Override
  MethodVisitor visitMethod(int access, String name, String desc, String signature,
      String[] exceptions) {
    def mv = cv.visitMethod(access, name, desc, signature, exceptions)
    if ("com/sloth/click/MainActivity" == this.mClassName) {
      if ("onCreate" == name) {
        logState("onCreate")
        return new SlothOnCreateVisitor(mv)
      } else if ("onDestroy" == name) {
        logState("onDestroy")
        return new SlothOnDestroyVisitor(mv)
      }
    }
    mv
  }

  @Override
  void visitEnd() {
    logState("end")
    super.visitEnd()
  }

  static void logState(String state) {
    println("visit -----------------------------------> $state")
  }
}