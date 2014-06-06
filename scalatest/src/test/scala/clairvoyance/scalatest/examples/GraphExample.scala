package clairvoyance.scalatest.examples

import clairvoyance.plugins.Graph
import org.scalatest.Spec

class GraphExample extends Spec with LdapAuthenticationContext with Graph {

  override def defaultActor = "Web Server"

  object `The Web Site must` {
    def `authenticate the user using LDAP`() {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
