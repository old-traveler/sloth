package com.sloth.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class SlothClassVisitor extends ClassVisitor {

  String mClassName

  boolean enableLife = true
  private def flags = 'BCDFIJSZL'
  private int lIndex = flags.length() - 1

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
    }else if (name.startsWith("lambda\$") && desc.endsWith("Landroid/view/View;)V") && desc.startsWith("(")) {
      def index= getOffsetByDesc(desc)
      if (access == 4106){
        index--
      }
      println("$mClassName   $name $desc $index  $access $signature $exceptions")
      SlothLogHelper.getDefault().appendLine("$mClassName --> $name")
      return new SlothOnClickVisitor(mv, mClassName + name,index)
    }
    mv
  }

  /**
   * 计算java lambda表达式中view参数的偏移量
   * @param desc
   * @return
   */
  private int getOffsetByDesc(String desc){
    def params = desc.substring(1,desc.length() - 21).split(";")
    int offset = 0
    params.each { String param  ->
      int index = 0
      while (index < param.length()){
        int position = flags.indexOf(param.charAt(index++).toString())
        if (position <= lIndex){
         offset++
        }
        if (position == lIndex){
          break
        }
      }
    }
    offset
  }


  @Override
  void visitEnd() {
    super.visitEnd()
  }

}