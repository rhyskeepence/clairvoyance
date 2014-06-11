package clairvoyance.specs2

import org.specs2.execute.Pending

class PendingExamplesSpec extends ClairvoyantSpec {
  "contain 'foo'" in pending {
    ok
  }
  "contain 'bar'" in pending("bar unavailable") {
    ko
  }
  "contain 'fu'"  in todo {
    ok
  }
  "contain 'baz'" in skipped {
    ko
  }
  "contain 'fu baz'" in {
    skipped("who's baz?")
  }
  "contain 'foo bar'" in {
    Pending("foo is at the bar")
  }
  "contain 'HELLO'" in {
    "Hello world" must contain("HELLO")
  }.pendingUntilFixed

  "contain 'WORLD'" in {
    "Hello world" must contain("WORLD")
  }.pendingUntilFixed("more brews")

  "contain 'wOrLd'" in {
    Pending("just can't be bothered")
  }
}
