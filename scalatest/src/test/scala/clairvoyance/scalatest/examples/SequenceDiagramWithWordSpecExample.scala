package clairvoyance.scalatest.examples

import clairvoyance.plugins.SequenceDiagram
import clairvoyance.scalatest.{SkipInteractions, SkipSpecification}
import org.scalatest.WordSpec

class SequenceDiagramWithWordSpecExample
    extends WordSpec
    with LdapAuthenticationContext
    with SequenceDiagram {

  override def defaultActor = "Web Server"
  def produce               = afterWord("produce")

  "User authentication" must produce {
    "spec + interactions" in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }

    "no spec" taggedAs SkipSpecification in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }

    "no interactions" taggedAs SkipInteractions in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }

    "no spec, no interactions" taggedAs (SkipSpecification, SkipInteractions) in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
