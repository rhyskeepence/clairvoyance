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
    "capture HTTP request with no content type" in new context {
      responseBodyFor("blah.dunno") must be_==("probably text")
    }
    "capture HTTP request with binary content" in new context {
      responseBodyFor("blah.pdf") must be_==("00001100000010101")
    }
    "capture HTTP POST request" in new context {
      responseBodyFor(url(proxyUrl).POST.addParameter("name", "Fred")) must be_==("your name is Fred")
    }
    "not capture HTTP request for excluded URLs" in new context {
      responseBodyFor(url(proxyUrl + "/excluded/blah.txt")) must be_==("response from downstream")
    }
  }

  trait context extends ClairvoyantContext with SequenceDiagram {

    override def capturedInputsAndOutputs = Seq(proxy)

    val http = new Http

    // TODO content negotiation
    val remoteServer = TestServer(Planify {
      case GET(Path("/blah.txt")) => PlainTextContent ~> ResponseString("response from downstream")
      case GET(Path("/blah.xml")) => ApplicationXmlContent ~> ResponseString("<wallaby>like a kangaroo but a different animal entirely.</wallaby>")
      case GET(Path("/blah.dunno")) => ResponseString("probably text")
      case GET(Path("/blah.pdf")) => PdfContent ~> ResponseString("00001100000010101")
      case GET(Path("/excluded/blah.txt")) => PlainTextContent ~> ResponseString("response from downstream")
      case POST(Params(params)) => PlainTextContent ~> ResponseString("your name is " + params("name").headOption.getOrElse("???"))
    })

    val proxy = RecordingHttpProxy.listeningOnAnyLocalPort("localhost", remoteServer.port, "System X", "System Y", Seq.empty, Seq("excluded".r))
    val proxyUrl = "http://localhost:" + proxy.listenPort
    
    remoteServer.start()
    proxy.start()

    def responseBodyFor(path: String) = {
      http(url(proxyUrl) / path OK as.String).apply()
    }
    
    def responseBodyFor(request: Req) = {
      http(request OK as.String).apply()
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
