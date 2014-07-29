package clairvoyance.export

object NaiveMultilineMethodNameJoiner {
  def join(strings: Seq[String]): Seq[String] = joinMultilineStrings(strings).map(doStripLine)

  private def joinMultilineStrings(content: Seq[String]): Seq[String] = {
    def quoteses(s: String): Int = "\"\"\"".r.findAllIn(s).length

    content.foldLeft(Seq.empty[String]) {
      case (accum, line) =>
        val last = accum.lastOption.getOrElse("")

        if (quoteses(last) == 1) accum.init :+ last + "\n" + line else accum :+ line
    }
  }

  private def doStripLine(s: String): String = {
    "\"\"\"(.*\n.*)*\"\"\".stripMargin".r.findFirstMatchIn(s).map { m =>
      m.group(0).stripMargin
    }.getOrElse(s)
  }
}
