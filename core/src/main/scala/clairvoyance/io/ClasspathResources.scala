package clairvoyance.io

import java.io.{BufferedInputStream, BufferedOutputStream, FileOutputStream}
import java.net.URL
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.nio.file.{Path, Paths, Files => JFiles}
import java.util.zip.{ZipEntry, ZipInputStream}

import scala.annotation.tailrec

object ClasspathResources {

  def copyResource(src: String, outputDir: String): Unit = {
    val selfUrl = getClass.getClassLoader.getResource(getClass.getName.replace(".", "/") + ".class")

    Option(selfUrl)
      .filter(_.getProtocol == "jar")
      .foreach(jar => {
        val jarUrl = new URL(jar.getPath.takeWhile(_ != '!').mkString)
        unjar(jarUrl, outputDir, ".*" + src + "/.*")
      })

    val directory = Path.of(getClass.getClassLoader.getResource(src).toURI)
    if (!directory.toString.startsWith("jar")) {
      val target = Path.of(outputDir, src)
			JFiles.walk(directory).forEach(source => {
				val targetFile = target.resolve(directory.relativize(source))
				JFiles.createDirectories(targetFile.getParent)
				JFiles.copy(source, targetFile, REPLACE_EXISTING)
			})
    }
  }

  private def unjar(jarUrl: URL, dirPath: String, regexFilter: String): Unit = {
    val path = Paths.get(dirPath)
    if (!JFiles.exists(path))
      JFiles.createDirectories(path)

    val zis = new ZipInputStream(new BufferedInputStream(jarUrl.openStream()))

    @tailrec
    def extractEntry(entry: ZipEntry): Unit = {
      if (entry != null) {
        if (entry.getName.matches(regexFilter)) {
          val destination = Path.of(dirPath, entry.getName)
          if (entry.isDirectory)
            JFiles.createDirectories(destination)
          else {
            JFiles.createFile(destination)
            val fos  = new FileOutputStream(dirPath + "/" + entry.getName)
            val dest = new BufferedOutputStream(fos, 2048)
            Stream.continually(zis.read()).takeWhile(_ > 0).foreach(dest.write)
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
