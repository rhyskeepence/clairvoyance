package clairvoyance.proxy

import clairvoyance.specs2._
import clairvoyance.plugins._
import unfiltered.netty.cycle._
import unfiltered.request._
import unfiltered.response._
import dispatch._

class ProxyExample extends ClairvoyantSpec {

  "HTTP Proxy" should {
    "capture HTTP request with text/plain content" in new context {
      responseBodyFor("blah.txt") must be_==("response from downstream")
    }
    "capture HTTP request with application/xml content" in new context {
      responseBodyFor("blah.xml") must be_==("<wallaby>like a kangaroo but a different animal entirely.</wallaby>")
    }
  }

  trait context extends ClairvoyantContext with SequenceDiagram {

    override def capturedInputsAndOutputs = Seq(proxy)

    val http = new Http

    // TODO content negotiation
    val remoteServer = TestServer(Planify {
      case GET(Path("/blah.txt")) => PlainTextContent ~> ResponseString("response from downstream")
      case GET(Path("/blah.xml")) => ApplicationXmlContent ~> ResponseString("<wallaby>like a kangaroo but a different animal entirely.</wallaby>")
    })

    val proxy = RecordingHttpProxy.listeningOnAnyLocalPort("localhost", remoteServer.port, "System X", "System Y")
    val proxyUrl = "http://localhost:" + proxy.listenPort
    
    remoteServer.start()
    proxy.start()

    def responseBodyFor(path: String) = {
      http(url(proxyUrl) / path OK as.String).apply()
    }

    override def tearDown() = {
      proxy.stop()
      remoteServer.stop()
      http.shutdown()
    }
  }

  case class TestServer(plan: Plan) {
    val server = unfiltered.netty.Server.anylocal.plan(plan)
    def port: Int = server.ports.head

    def start(): Unit = {
      server.start()
    }

    def stop(): Unit = {
      server.stop()
    }
  }
}
