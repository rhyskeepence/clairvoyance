package org.specs2.clairvoyance

import org.junit.runner.notification.RunNotifier
import org.junit.runner._
import org.specs2.internal.scalaz.Scalaz
import output.ClairvoyanceHtmlExporting
import Scalaz._
import org.specs2.main.Arguments
import org.specs2.execute._
import org.specs2.reflect.Classes._
import org.specs2.specification._
import junit.framework.ComparisonFailure
import org.specs2.reporter.DescriptionAndExamples
import org.specs2.reporter.DefaultSelection.select
import org.specs2.runner.{SpecFailureAssertionFailedError, JUnitDescriptionsFragments, JUnitRunner}
import org.specs2.control.Throwablex
import org.specs2.text.AnsiColors

class ClairvoyanceRunner(klass: Class[_]) extends JUnitRunner(klass) {
  private val executor = new FragmentExecution {}
  private val descriptions = new JUnitDescriptionsFragments(klass.getName)
  private lazy val DescriptionAndExamples(desc, executions) = descriptions.foldAll(select(content.fragments))

  override protected lazy val specification = tryToCreateObject[ClairvoyantSpec](klass.getName).get
  val clairvoyanceExporter = new ClairvoyanceHtmlExporting(specification)

  override def run(notifier: RunNotifier) {
    executeSpecification |> export |> notifyJUnit(notifier)
  }

  private def export = (executed: Seq[(Description, ExecutedFragment)]) => {
    val commandLineArgs = properties.getProperty("commandline").getOrElse("").split("\\s")
    val arguments = Arguments(commandLineArgs: _*) <| args

    clairvoyanceExporter.export(arguments)(ExecutingSpecification.create(specification.content.specName, executed.map(_._2)))

    executed
  }

  /* Nothing below here has changed from JUnitRunner, but it's private */

  private def executeSpecification =
    executions.collect {
      case (desc, f@SpecStart(_, _, _, _)) => (desc, executor.executeFragment(args)(f))
      case (desc, f@Example(_, _)) => (desc, executor.executeFragment(args)(f))
      case (desc, f@Text(_)) => (desc, executor.executeFragment(args)(f))
      case (desc, f@Step(_)) => (desc, executor.executeFragment(args)(f))
      case (desc, f@Action(_)) => (desc, executor.executeFragment(args)(f))
      case (desc, f@SpecEnd(_)) => (desc, executor.executeFragment(args)(f))
    }

  private def notifyJUnit(notifier: RunNotifier) = (executed: Seq[(Description, ExecutedFragment)]) => {
    executed foreach {
      case (desc, ExecutedResult(_, result, timer, _, _)) => {
        notifier.fireTestStarted(desc)
        result match {
          case f@Failure(m, e, st, d) =>
            notifier.fireTestFailure(new notification.Failure(desc, junitFailure(f)))
          case e@Error(m, st) =>
            notifier.fireTestFailure(new notification.Failure(desc, args.traceFilter(e.exception)))
          case DecoratedResult(_, f@Failure(m, e, st, d)) =>
            notifier.fireTestFailure(new notification.Failure(desc, junitFailure(f)))
          case DecoratedResult(_, e@Error(m, st)) =>
            notifier.fireTestFailure(new notification.Failure(desc, args.traceFilter(e.exception)))
          case Pending(_) | Skipped(_, _) => notifier.fireTestIgnored(desc)
          case Success(_) | DecoratedResult(_, _) => ()
        }
        notifier.fireTestFinished(desc)
      }
      case (desc, ExecutedSpecStart(_, _, _)) => notifier.fireTestRunStarted(desc)
      case (desc, ExecutedSpecEnd(_, _, _)) => notifier.fireTestRunFinished(new org.junit.runner.Result)
      case (desc, _) => // don't do anything otherwise too many tests will be counted
    }
  }

  private def junitFailure(f: Failure)(implicit args: Arguments): Throwable = f match {
    case Failure(m, e, st, NoDetails()) =>
      new SpecFailureAssertionFailedError(Throwablex.exception(AnsiColors.removeColors(m), args.traceFilter(st)))

    case Failure(m, e, st, FailureDetails(expected, actual)) => new ComparisonFailure(AnsiColors.removeColors(m), expected, actual) {
      private val e = args.traceFilter(f.exception)

      override def getStackTrace = e.getStackTrace

      override def getCause = e.getCause

      override def printStackTrace = e.printStackTrace

      override def printStackTrace(w: java.io.PrintStream) = e.printStackTrace(w)

      override def printStackTrace(w: java.io.PrintWriter) = e.printStackTrace(w)
    }
  }

}