package clairvoyance.export

import org.specs2.main.Arguments
import org.specs2.specification.{ExecutedText, ExecutedResult, ExecutedSpecification}

trait TeamCityTestReporter {

  private lazy val teamCityProjectName = Option(System.getenv("TEAMCITY_PROJECT_NAME"))

  def printTeamCityLog(spec: ExecutedSpecification)(implicit args: Arguments): Unit = {
    spec.fragments.foldLeft("") {
      (latestHeading, fragment) =>
        fragment match {
          case executedResult: ExecutedResult =>
            val testName = latestHeading + " " + executedResult.s.toString
            teamcityReport("testStarted", "name" -> testName)

            if (executedResult.isIssue) {
              teamcityReport("testFailed",
                "name" -> testName,
                "details" -> ("Expected " + executedResult.result.expected + ", but " + executedResult.result.message)
              )
            }

            if (executedResult.isSuspended) {
              teamcityReport("testIgnored", "name" -> testName)
            }

            teamcityReport("testFinished",
              "name" -> testName,
              "duration" -> executedResult.stats.timer.totalMillis.toString
            )

            latestHeading

          case executedText: ExecutedText => executedText.text
          case _ => latestHeading
        }
    }
  }

  // http://confluence.jetbrains.net/display/TCD65/Build+Script+Interaction+with+TeamCity
  private def tidy(s: String) = s
    .replace("|", "||")
    .replace("'", "|'")
    .replace("\n", "|n")
    .replace("\r", "|r")
    .replace("\u0085", "|x")
    .replace("\u2028", "|l")
    .replace("\u2029", "|p")
    .replace("[", "|[")
    .replace("]", "|]")

  private def teamcityReport(messageName: String, attributes: (String, String)*): Unit = {
    if (shouldLog) {
      val attributeString = attributes.map {
        case (k, v) => k + "='" + tidy(v) + "'"
      }.mkString(" ")

      printf("##teamcity[%s %s]\n", messageName, attributeString)
    }
  }

  private def shouldLog = teamCityProjectName.isDefined
}
