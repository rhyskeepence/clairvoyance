package clairvoyance.scalatest.examples

import clairvoyance.plugins.SequenceDiagram
import clairvoyance.scalatest.tags.skipInteractions
import clairvoyance.scalatest.{SkipInteractions, SkipSpecification}
import org.scalatest.WordSpec

class SequenceDiagramWithWordSpecExample extends WordSpec with LdapAuthenticationContext with SequenceDiagram {

  override def defaultActor = "Web Server"

  "The Web Site" must {
    "authenticate the user using LDAP" in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }

    "authenticate the user using LDAP (no spec)" taggedAs SkipSpecification in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }

    "authenticate the user using LDAP (no interactions)" taggedAs SkipInteractions in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }

    "authenticate the user using LDAP (no spec, no interactions)" taggedAs (SkipSpecification, SkipInteractions) in {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }
  }
}
