package com.sloth.plugin

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor

import static org.objectweb.asm.Opcodes.*

class SlothOnClickVisitor extends MethodVisitor {

  SlothOnClickVisitor(MethodVisitor mv) {
    super(ASM4, mv)
  }

  @Override
  void visitCode() {
    super.visitCode()
    mv.visitVarInsn(ALOAD, 1)
    mv.visitMethodInsn(INVOKESTATIC, "com/sloth/click_util/ClickHelper",
        "isFastClick", "(Landroid/view/View;)Z", false)
    mv.visitVarInsn(ISTORE, 2)
    Label l1 = new Label()
    mv.visitLabel(l1)
    mv.visitVarInsn(ILOAD, 2)
    Label l2 = new Label()
    mv.visitJumpInsn(IFEQ, l2)
    mv.visitInsn(RETURN)
    mv.visitLabel(l2)
  }
}