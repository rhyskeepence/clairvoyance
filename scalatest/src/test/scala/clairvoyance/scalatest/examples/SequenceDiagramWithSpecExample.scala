package clairvoyance.scalatest.examples

import clairvoyance.plugins.SequenceDiagram
import clairvoyance.scalatest.tags.{skipSpecification, skipInteractions}
import org.scalatest.Spec

class SequenceDiagramWithSpecExample extends Spec with LdapAuthenticationContext with SequenceDiagram {

  override def defaultActor = "Web Server"

  object `The Web Site must` {
    def `authenticate the user using LDAP`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }

    @skipSpecification
    def `authenticate the user using LDAP (no spec)`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }

    @skipInteractions
    def `authenticate the user using LDAP (no interactions)`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }

    @skipSpecification
    @skipInteractions
    def `authenticate the user using LDAP (no spec, no interactions)`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
