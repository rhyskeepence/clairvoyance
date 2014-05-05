package clairvoyance.io

import java.io.File

object Files {
  def listFiles(f: File): Stream[File] =
    f #:: (if (f.isDirectory) f.listFiles().toStream.flatMap(listFiles) else Stream.empty)

  def currentWorkingDirectory = new File(System.getProperty("user.dir"))
}
