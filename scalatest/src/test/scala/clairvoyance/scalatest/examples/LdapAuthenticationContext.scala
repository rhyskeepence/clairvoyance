package clairvoyance.scalatest.examples

import clairvoyance.ProducesCapturedInputsAndOutputs
import clairvoyance.scalatest.ClairvoyantContext
import org.scalatest.Suite

trait LdapAuthenticationContext extends ClairvoyantContext { this: Suite =>
  val ldap = new Ldap
  val webServer = new WebServer(ldap)

  def whenTheUserLogsInToTheWebSiteUsingTheCredentials(user: String, password: String): Unit = {
    webServer.login(user, password)
  }

  def thenTheUserIsShownSecrets(): Unit = ()

  override def capturedInputsAndOutputs = Seq(webServer, ldap)

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
