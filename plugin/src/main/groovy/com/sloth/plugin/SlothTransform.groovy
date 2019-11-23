package com.sloth.plugin

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter



class SlothTransform extends Transform {

  def fileHandler = new FileHandler()

  SlothTransform(){

  }

  @Override
  String getName() {
    return SlothTransform.class.name
  }

  @Override
  Set<QualifiedContent.ContentType> getInputTypes() {
    return TransformManager.CONTENT_CLASS
  }

  @Override
  Set<? super QualifiedContent.Scope> getScopes() {
    return TransformManager.SCOPE_FULL_PROJECT
  }

  @Override
  boolean isIncremental() {
    return false
  }

  @Override
  void transform(TransformInvocation transformInvocation)
      throws TransformException, InterruptedException, IOException {
    super.transform(transformInvocation)
    def startTime = System.currentTimeMillis()
    Collection<TransformInput> inputs = transformInvocation.inputs
    def outputProvider = transformInvocation.outputProvider
    //TODO 处理增量编译
    if (outputProvider != null) {
      outputProvider.deleteAll()
    }
    //遍历inputs
    inputs.each {
      it.directoryInputs.each { directoryInput ->
        fileHandler.handleDirectoryInput directoryInput, outputProvider, { data ->
          classVisitor(data)
        }
      }

      it.jarInputs.each { jarInput ->
        fileHandler.handleJarInput jarInput, outputProvider, { data ->
          classVisitor(data)
        }
      }
    }
    print "============================== sloth transform end cost : ${System.currentTimeMillis() - startTime}============================="
  }

  static byte[] classVisitor(byte[] bytes) {
    def classReader = new ClassReader(bytes)
    def classWriter = new ClassWriter(classReader,
        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)
    def classVisitor = new SlothClassVisitor(classWriter)
    try {
      classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
    } catch(Exception e){
//      e.printStackTrace()
    }
    classWriter.toByteArray()
  }


}