package clairvoyance.scalatest.examples

import clairvoyance.plugins.SequenceDiagram
import clairvoyance.scalatest.{SkipInteractions, SkipSpecification}
import org.scalatest.FunSuite

class SequenceDiagramWithFunSuiteExample
    extends FunSuite
    with LdapAuthenticationContext
    with SequenceDiagram {

  override def defaultActor = "Web Server"

  test("The Web Site must authenticate the user using LDAP") {
    whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
    thenTheUserIsShownSecrets()
  }

  test("The Web Site must authenticate the user using LDAP (no spec)", SkipSpecification) {
    whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
    thenTheUserIsShownSecrets()
  }

  test("The Web Site must authenticate the user using LDAP (no interactions)", SkipInteractions) {
    whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
    thenTheUserIsShownSecrets()
  }

  test(
    "The Web Site must authenticate the user using LDAP (no spec, no interactions)",
    SkipSpecification,
    SkipInteractions
  ) {
    whenTheUserLogsInToTheWebSiteUsingTheCredentials(user = "mario", password = "luigi")
    thenTheUserIsShownSecrets()
  }
}
