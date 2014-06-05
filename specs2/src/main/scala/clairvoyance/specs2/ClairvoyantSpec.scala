package clairvoyance.specs2

import org.specs2.mutable.Specification

abstract class ClairvoyantSpec extends Specification {
  sequential
  args.report(exporter = "clairvoyance.specs2.export.ClairvoyanceHtmlExporting")
}
