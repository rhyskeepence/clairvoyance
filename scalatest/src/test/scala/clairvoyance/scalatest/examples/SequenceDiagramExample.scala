package clairvoyance.scalatest.examples

import org.scalatest.Spec
import clairvoyance.plugins.SequenceDiagram

class SequenceDiagramExample extends Spec with LdapAuthenticationContext with SequenceDiagram {

  override def defaultActor = "Web Server"

  object `The Web Site must` {
    def `authenticate the user using LDAP`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
