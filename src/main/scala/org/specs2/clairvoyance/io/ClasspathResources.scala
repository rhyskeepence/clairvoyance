package org.specs2.clairvoyance.io

import java.net.URL
import java.util.zip.{ZipEntry, ZipInputStream}
import java.io.{BufferedOutputStream, FileOutputStream, BufferedInputStream}
import org.specs2.io.fs

object ClasspathResources {

  def copyResource(src: String, outputDir: String) {
    val selfUrl = Thread.currentThread.getContextClassLoader.getResource(getClass.getName.replace(".", "/")+".class")
    for (url <- Option(selfUrl) if url.getProtocol == "jar") {
      val jarUrl = new URL(url.getPath.takeWhile(_ != '!').mkString)
      unjar(jarUrl, outputDir, ".*" + src + "/.*")
    }

    val folderUrl = Thread.currentThread.getContextClassLoader.getResource(src)
    for (url <- Option(folderUrl) if !folderUrl.toString.startsWith("jar"))
      fs.copyDir(url, outputDir + src)
  }

  private def unjar(jarUrl: URL, dirPath: String, regexFilter: String) {
    fs.mkdirs(dirPath)
    val uis = jarUrl.openStream()
    val zis = new ZipInputStream(new BufferedInputStream(uis))

    @annotation.tailrec
    def extractEntry(entry: ZipEntry) {
      if (entry != null) {
        if (entry.getName.matches(regexFilter)) {
          if (entry.isDirectory()) {
            fs.createDir(dirPath + "/" + entry.getName)
          } else {
            fs.createFile(dirPath + "/" + entry.getName)
            val fos = new FileOutputStream(dirPath + "/" + entry.getName)
            val dest = new BufferedOutputStream(fos, 2048)
            fs.copy(zis, dest)
            dest.flush
            dest.close
          }

        }
        extractEntry(zis.getNextEntry)
      }
    }
    extractEntry(zis.getNextEntry)
    zis.close
  }
}