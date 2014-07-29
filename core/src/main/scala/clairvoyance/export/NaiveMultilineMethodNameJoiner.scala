package clairvoyance.export

object NaiveMultilineMethodNameJoiner {
  def join(strings: Seq[(String, Int)]): Seq[(String, Int)] = joinMultilineStrings(strings).map {
    case (line, number) => (doStripLine(line), number)
  }

  private def joinMultilineStrings(content: Seq[(String, Int)]): Seq[(String, Int)] = {
    def quoteses(s: String): Int = "\"\"\"".r.findAllIn(s).length

    content.foldLeft(Seq.empty[(String,Int)]) {
      case (accum, line) =>
        val last = accum.lastOption.getOrElse(("",0))

        if (quoteses(last._1) == 1) accum.init :+ (last._1 + "\n" + line._1, line._2) else accum :+ line
    }
  }

  private def doStripLine(s: String): String = {
    "\"\"\"(.*\n.*)*\"\"\".stripMargin".r.findFirstMatchIn(s).map { m =>
      m.group(0).stripMargin
    }.getOrElse(s)
  }
}
