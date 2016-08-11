package clairvoyance.scalatest.examples

import clairvoyance.plugins.Graph
import org.scalatest.WordSpec

class GraphExample extends WordSpec with LdapAuthenticationContext with Graph {

  override def defaultActor = "Web Server"

  "The Web Site" must {
    "authenticate the user using LDAP" in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
