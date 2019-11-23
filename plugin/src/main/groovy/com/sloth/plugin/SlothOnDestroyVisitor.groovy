package com.sloth.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SlothOnDestroyVisitor extends MethodVisitor{

  String className

  SlothOnDestroyVisitor(MethodVisitor mv,String className) {
    super(Opcodes.ASM4, mv)
    this.className = className
  }

  @Override
  void visitCode() {
    super.visitCode()
    //方法执行前插入
    mv.visitLdcInsn("helper")
    mv.visitLdcInsn("-----------> onDestroy "+className)
    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e",
        "(Ljava/lang/String;Ljava/lang/String;)I", false)
    mv.visitInsn(Opcodes.POP)
  }

  @Override
  void visitInsn(int opcode) {
    //方法后插入
    super.visitInsn(opcode)
  }
}