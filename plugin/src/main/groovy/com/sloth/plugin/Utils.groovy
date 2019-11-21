package com.sloth.plugin

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class Utils {

  static void modifyFile(File file) {
    String name = file.name
    // 修改非系统自动生成的 .class 文件
    println("name---------!!!!!!!!!!"+name)
    if (name.endsWith(".class")) {
      def src = file.getBytes()
      byte[] code = src
      if (!name.startsWith("R\$") && !"R.class".equals(name) && !"BuildConfig.class".equals(name)) {
        //println "class in Directory: " + name + " is changing ......"
        code = visitAndReturnCode(src)
      }
      FileOutputStream fos = new FileOutputStream(
          file.parentFile.absolutePath + File.separator + name)
      fos.write(code)
      fos.close()
    }
  }

  static File modifyJar(File jarFile, File tempDir, String hexedName) {

    def file = new JarFile(jarFile)
    def outputJar = new File(tempDir, hexedName + "_" + jarFile.name + ".tmp")

    println "outputJar name = " + outputJar.getName()

    JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(outputJar))
    Enumeration enumeration = file.entries()

    while (enumeration.hasMoreElements()) {

      JarEntry jarEntry = (JarEntry) enumeration.nextElement()
      if (jarEntry.isDirectory()) continue

      String entryName = jarEntry.getName()

      ZipEntry zipEntry = new ZipEntry(entryName)
      jarOutputStream.putNextEntry(zipEntry)

      InputStream inputStream = file.getInputStream(jarEntry)
      byte[] modifiedClassBytes = null
      byte[] sourceClassBytes = IOUtils.toByteArray(inputStream)

      if (entryName.endsWith(".class")) {
        //println "class in Jar: " + path2Classname(entryName) + " is changing ......"
        modifiedClassBytes = visitAndReturnCode(sourceClassBytes)
      }

      if (modifiedClassBytes == null) {
        jarOutputStream.write(sourceClassBytes)
      } else {
        jarOutputStream.write(modifiedClassBytes)
      }

      jarOutputStream.closeEntry()
    }
    jarOutputStream.close()
    file.close()

    return outputJar
  }

  static byte[] visitAndReturnCode(byte[] bytes) {

    def weavedBytes = bytes

    ClassReader classReader = new ClassReader(bytes)
    ClassWriter classWriter = new ClassWriter(classReader,
        ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS)

    def classAdapter = new SlothClassVisitor(classWriter)
    try {
      classReader.accept(classAdapter, ClassReader.EXPAND_FRAMES)
      weavedBytes = classWriter.toByteArray()
    } catch (Exception e) {
      println "Exception occured when visit code \n " + e.printStackTrace()
    }

    return weavedBytes
  }

  private static String path2Classname(String entryName) {
    entryName.replace(File.separator, ".").replace(".class", "")
  }
}