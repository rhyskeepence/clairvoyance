package clairvoyance.scalatest.examples

import clairvoyance.plugins.SequenceDiagram
import clairvoyance.scalatest.tags.{skipSpecification, skipInteractions}
import org.scalatest.refspec.RefSpec

class SequenceDiagramWithRefSpecExample
    extends RefSpec
    with LdapAuthenticationContext
    with SequenceDiagram {

  override def defaultActor = "Web Server"

  object `User authentication must produce` {
    def `spec + interactions`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }

    @skipSpecification
    def `no spec`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }

    @skipInteractions
    def `no interactions`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }

    @skipSpecification
    @skipInteractions
    def `no spec, no interactions`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
