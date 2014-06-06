package clairvoyance.specs2.examples

import clairvoyance.ProducesCapturedInputsAndOutputs
import clairvoyance.plugins.Graph
import clairvoyance.specs2.{ClairvoyantSpec, ClairvoyantContext}
import org.specs2.execute.Success

class GraphExample extends ClairvoyantSpec {

  "The Web Site" should {
    "authenticate the user using LDAP" in new context {
      whenTheUserLogsInToTheWebSiteUsingTheCredentials(user="mario", password="luigi")
      thenTheUserIsShownSecrets()
    }
  }

  trait context extends ClairvoyantContext with Graph {
    val ldap = new Ldap
    val webServer = new WebServer(ldap)

    override def defaultActor = "Web Server"

    def whenTheUserLogsInToTheWebSiteUsingTheCredentials(user: String, password: String): Unit = {
      webServer.login(user, password)
    }

    def thenTheUserIsShownSecrets(): Unit = Success

    override def capturedInputsAndOutputs = Seq(webServer, ldap)
  }

  class WebServer(ldap: Ldap) extends ProducesCapturedInputsAndOutputs {
    def login(user: String, password: String): Unit = {
      captureValue("Login Submitted from User" -> s"user: $user, password: $password")
      ldap.authenticate(user, password)
      captureValue("Response to User" -> "Forwarding to showSecrets.html")
    }
  }

  class Ldap extends ProducesCapturedInputsAndOutputs {
    def authenticate(user: String, password: String): Unit = {

      captureValue("Username and Password to LDAP" ->
        <authRequest>
          <user>{user}</user>
          <pass>{password}</pass>
        </authRequest>
      )
      // do something

      captureValue("Credentials for User from LDAP" ->
        <authResponse>
          <user>
            <name>{user}</name>
            <credentials>
              <login/>
              <viewWibbles/>
              <changeWibbles/>
              <admin/>
            </credentials>
          </user>
        </authResponse>)
    }
  }
}
