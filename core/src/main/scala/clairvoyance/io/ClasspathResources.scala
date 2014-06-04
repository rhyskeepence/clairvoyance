package clairvoyance.io

import java.io.{FileOutputStream, BufferedOutputStream, BufferedInputStream}
import java.net.URL
import java.util.zip.{ZipEntry, ZipInputStream}
import scala.annotation.tailrec
import scalax.file.Path
import scalax.io.support.FileUtils

object ClasspathResources {

  def copyResource(src: String, outputDir: String): Unit = {
    val selfUrl = getClass.getClassLoader.getResource(getClass.getName.replace(".", "/") + ".class")
    for (url <- Option(selfUrl) if url.getProtocol == "jar") {
      val jarUrl = new URL(url.getPath.takeWhile(_ != '!').mkString)
      unjar(jarUrl, outputDir, ".*" + src + "/.*")
    }

    val folderUrl = getClass.getClassLoader.getResource(src)
    for (path <- Path(folderUrl.toURI) if !folderUrl.toString.startsWith("jar")) {
      val target = Path.fromString(outputDir + src)
      target.deleteRecursively(force = true)
      path.copyTo(target)
    }
  }

  private def unjar(jarUrl: URL, dirPath: String, regexFilter: String): Unit = {
    Path.fromString(dirPath).createDirectory(createParents = true, failIfExists = false)
    val zis = new ZipInputStream(new BufferedInputStream(jarUrl.openStream()))

    @tailrec
    def extractEntry(entry: ZipEntry): Unit = {
      if (entry != null) {
        if (entry.getName.matches(regexFilter)) {
          val destination = Path.fromString(dirPath + "/" + entry.getName)
          if (entry.isDirectory)
            destination.createDirectory(createParents = true, failIfExists = false)
          else {
//            Resource.fromInputStream(zis).copyDataTo(destination)
            destination.createFile(createParents = true, failIfExists = false)
            val fos = new FileOutputStream(dirPath + "/" + entry.getName)
            val dest = new BufferedOutputStream(fos, 2048)
            FileUtils.copy(zis, dest)
            dest.flush()
            dest.close()
          }
        }
        extractEntry(zis.getNextEntry)
      }
    }
    extractEntry(zis.getNextEntry)
    zis.close()
  }
}
