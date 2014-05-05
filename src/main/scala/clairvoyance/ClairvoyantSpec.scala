package clairvoyance

import org.specs2.mutable.Specification

abstract class ClairvoyantSpec extends Specification {
  sequential
  args.report(exporter = "clairvoyance.export.ClairvoyanceHtmlExporting")
}
