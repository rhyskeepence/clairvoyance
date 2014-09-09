package clairvoyance.scalatest.export.single

import org.scalatest.{Args, Status, Suite, SuiteMixin}

trait ClairvoyantSpec extends SuiteMixin { this: Suite =>
  abstract override def run(testName: Option[String], args: Args) = {
    val reporter: ScalaTestHtmlReporter = new ScalaTestHtmlReporter
    val status: Status = super.run(testName, args.copy(reporter = new ForwardingReporter(args.reporter, reporter)))
    reporter.done()
    status
  }
}
