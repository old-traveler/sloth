package com.sloth.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SlothOnCreateVisitor extends MethodVisitor{

  SlothOnCreateVisitor(MethodVisitor mv) {
    super(Opcodes.ASM4, mv)
  }


  @Override
  void visitCode() {
    super.visitCode()
    //方法执行前插入
    mv.visitLdcInsn("helper")
    mv.visitLdcInsn("-----------> MainActivity onCreate")
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