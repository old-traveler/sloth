package com.sloth.plugin

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor

import static org.objectweb.asm.Opcodes.*

class SlothOnClickVisitor extends MethodVisitor {

  String className

  def canFastClick = false

  int offset = 0

  SlothOnClickVisitor(MethodVisitor mv, String className) {
    super(ASM4, mv)
    this.className = className
  }

  SlothOnClickVisitor(MethodVisitor mv, String className, int offset) {
    super(ASM4, mv)
    this.className = className
    this.offset = offset
  }

  @Override
  AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    def annotation = super.visitAnnotation(desc, visible)
    canFastClick = canFastClick || desc == "Lcom/sloth/click_util/FastClick;"
    annotation
  }

  @Override
  void visitCode() {
    super.visitCode()
    if (canFastClick){
      println("${className} has FastClick skip visitCode")
      //添加了快速点击的注解
      return
    }
    mv.visitVarInsn(ALOAD, 0)
    mv.visitVarInsn(ALOAD, 1 + offset)
    mv.visitLdcInsn(className)
    mv.visitMethodInsn(INVOKESTATIC, SlothTransform.slothClickConfig.clickHelperName,
        "isFastClick", "(Landroid/view/View;Ljava/lang/String;)Z", false)
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