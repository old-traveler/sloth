package com.sloth.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformOutputProvider
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class FileHandler {

  private List<Closure<Boolean>> mRules = new ArrayList<>()

  void addRule(Closure<Boolean> rule) {
    mRules.add(rule)
  }

  void handleDirectoryInput(DirectoryInput directoryInput,
      TransformOutputProvider outputProvider, Closure closure) {
    if (directoryInput.file.isDirectory()) {
      directoryInput.file.eachFileRecurse {
        if (checkClassFile(it.name)) {
          def code = closure(it.bytes)
          def fos = new FileOutputStream(it.parentFile.absolutePath + File.separator + it.name)
          fos.write(code)
          fos.close()
        }
      }
    }
    def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes,
        directoryInput.scopes,
        Format.DIRECTORY)
    FileUtils.copyDirectory(directoryInput.file, dest)
  }

  void handleJarInput(JarInput jarInput, TransformOutputProvider outputProvider,
      Closure closure) {
    if (jarInput.file.getAbsolutePath().endsWith('.jar')) {
      //重名输出文件,因为可能同名,会覆盖
      def jarName = jarInput.name
      def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
      if (jarName.endsWith(".jar")) {
        jarName = jarName.substring(0, jarName.length() - 4)
      }
      def jarFile = new JarFile(jarInput.file)
      def enumeration = jarFile.entries()
      def tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_tmp.jar")
      if (tmpFile.exists()) {
        tmpFile.delete()
      }
      def jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
      while (enumeration.hasMoreElements()) {
        JarEntry jarEntry = enumeration.nextElement()
        def zipEntry = new ZipEntry(jarEntry.name)
        def inputStream = jarFile.getInputStream(jarEntry)
        if (checkClassFile(jarEntry.name)) {
          jarOutputStream.putNextEntry(zipEntry)
          jarOutputStream.write(closure(IOUtils.toByteArray(inputStream)))
        } else {
          jarOutputStream.putNextEntry(zipEntry)
          jarOutputStream.write(IOUtils.toByteArray(inputStream))
        }
        jarOutputStream.closeEntry()
      }

      jarOutputStream.close()
      def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes,
          jarInput.scopes, Format.JAR)
      FileUtils.copyFile(tmpFile, dest)
      tmpFile.delete()
    }
  }

  private boolean checkClassFile(String name) {
    return (name.endsWith(".class") && !name.startsWith("R\$") &&
        "R.class" !=
        name &&
        "BuildConfig.class" !=
        name &&
        checkRules(name))
  }

  private boolean checkRules(String name){
    boolean res = true
    mRules.each {
      res = res && it(name)
    }
    return res
  }
}