package com.sloth.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.Status
import com.android.build.api.transform.TransformOutputProvider
import com.google.common.io.Files
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class FileHandler {

  private List<Closure<Boolean>> mRules = new ArrayList<>()

  void addRules(List<Closure<Boolean>> rules) {
    mRules.addAll(rules)
  }

  void handleDirectoryInput(DirectoryInput directoryInput,
      TransformOutputProvider outputProvider, boolean isIncremental, Closure closure) {
    def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
        directoryInput.scopes,
        Format.DIRECTORY)
    if (directoryInput.file.isDirectory()) {
      if (isIncremental) {
        //        FileUtils.forceMkdir(dest)
        //        println("<<<<isIncremental")
        def changeFiles = directoryInput.changedFiles
        //        changeFiles.each {
        //          String destFilePath = dest.getAbsolutePath() + "/"+it.key.name
        //          println("${dest.getAbsolutePath()}   ${it.key.name}")
        //          File singleDest = new File(destFilePath)
        //          if (checkClassFile(it.key.name) && needTransform(it.key.name, it.value, singleDest)) {
        //
        //            def code = closure(it.key.name, it.key.bytes)
        //            def fos = new FileOutputStream(singleDest)
        //            fos.write(code)
        //            fos.close()
        //          }
        //        }

        directoryInput.file.eachFileRecurse {
          def status = changeFiles.get(it)
          def singleDest = new File(it.parentFile.absolutePath + File.separator + it.name)
          if (status != null && checkClassFile(it.name)) {
            println("找到 ${changeFiles.get(it).name()}")
            if (needTransform(it.name,status,singleDest)){
              println("修改")
              def code = closure(it.name, it.bytes)
              def fos = new FileOutputStream(singleDest)
              fos.write(code)
              fos.close()
            }

          }
          //          if (checkClassFile(it.name)) {
          //            def code = closure(it.name, it.bytes)
          //            def fos = new FileOutputStream(it.parentFile.absolutePath + File.separator + it.name)
          //            fos.write(code)
          //            fos.close()
          //          }
        }
      } else {
        println("<<<<NotIncremental")
        directoryInput.file.eachFileRecurse {
          if (checkClassFile(it.name)) {
            def code = closure(it.name, it.bytes)
            def fos = new FileOutputStream(it.parentFile.absolutePath + File.separator + it.name)
            fos.write(code)
            fos.close()
          }
        }
      }
    }
    FileUtils.copyDirectory(directoryInput.file, dest)
  }

  void handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider,
      boolean isIncremental,
      Closure closure) {
    if (jarInput.file.getAbsolutePath().endsWith('.jar')) {
      def dest = outputProvider.getContentLocation(
          jarInput.getFile().getAbsolutePath(),
          jarInput.getContentTypes(),
          jarInput.getScopes(),
          Format.JAR)
      Status status = jarInput.getStatus()
      if (isIncremental && !needTransform(jarInput.file.name, status, dest)) {
        return
      }
      def jarFile = new JarFile(jarInput.file)
      def enumeration = jarFile.entries()
      def jarOutputStream = new JarOutputStream(new FileOutputStream(dest))
      while (enumeration.hasMoreElements()) {
        JarEntry jarEntry = enumeration.nextElement()
        def zipEntry = new ZipEntry(jarEntry.name)
        def inputStream = jarFile.getInputStream(jarEntry)
        if (checkClassFile(jarEntry.name)) {
          jarOutputStream.putNextEntry(zipEntry)
          jarOutputStream.write(closure(jarInput.name, IOUtils.toByteArray(inputStream)))
        } else {
          jarOutputStream.putNextEntry(zipEntry)
          jarOutputStream.write(IOUtils.toByteArray(inputStream))
        }
        jarOutputStream.closeEntry()
      }

      jarOutputStream.close()
    }
  }

  private static boolean needTransform(String fileName, Status status, File dest) {
    switch (status) {
      case Status.ADDED: break
      case Status.CHANGED:

        //        println("$fileName  is added or change need transform")
        break
      case Status.NOTCHANGED:
        //        println("$fileName  is not change not need transform")
        return false
      case Status.REMOVED:
        //        println("$fileName  is remove not need transform")
        if (dest.exists()) {
          try {
            FileUtils.forceDelete(dest)
          } catch (FileNotFoundException e) {
            e.printStackTrace()
          }
        }
        return false
    }
    return true
  }

  private boolean checkClassFile(String name) {
    return (name.endsWith(".class") && !name.startsWith("R\$") &&
        "R.class" !=
        name &&
        "BuildConfig.class" !=
        name &&
        checkRules(name))
  }

  private boolean checkRules(String name) {
    boolean res = true
    mRules.each {
      res = res && it(name)
    }
    return res
  }
}