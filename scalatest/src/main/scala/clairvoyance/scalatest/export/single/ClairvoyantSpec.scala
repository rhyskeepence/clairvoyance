package clairvoyance.scalatest.export.single

import org.scalatest.{Args, Status, Suite, SuiteMixin}

trait ClairvoyantSpec extends SuiteMixin { this: Suite =>
  abstract override def run(testName: Option[String], args: Args): Status = {
    if(testName.isEmpty) { // so we don't produce a report for every test when OneInstancePerTest
      val reporter: ScalaTestHtmlReporter = new ScalaTestHtmlReporter
      val status: Status = super.run(testName, args.copy(reporter = new ForwardingReporter(args.reporter, reporter)))
      reporter.done()
      status
    } else super.run(testName, args)
  }
}
