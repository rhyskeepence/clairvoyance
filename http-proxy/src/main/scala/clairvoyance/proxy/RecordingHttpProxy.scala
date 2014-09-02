package clairvoyance.proxy

import unfiltered.netty
import unfiltered.util
import unfiltered.request._
import unfiltered.response._
import dispatch._
import com.ning.http.client.Response
import clairvoyance.ProducesCapturedInputsAndOutputs
import scala.xml.XML
import scala.concurrent.ExecutionContext
import scalax.io.Resource

// TODO - proxy all request headers (or at least accept, content-type and cookies)
// TODO - proxy all response headers
// TODO - deal with unsupported verbs better
// TODO - store binary/unknown response bodies - download to file and link to binary in captured value

case class RecordingHttpProxy(listenPort: Int, remoteHost: String, remotePort: Int, from: String, to: String)(implicit executor: ExecutionContext) extends ProducesCapturedInputsAndOutputs {

  private val client = new Http
  private val baseUrl = "http://" + remoteHost + ":" + remotePort

  private val plan = netty.future.Planify {
    case GET(req) =>
      captureValue("HTTP GET Request from " + from + " to " + to -> ("GET " + req.uri))
      client(url(baseUrl + req.uri).GET > CaptureAndProxyResponse)

    case PUT(req) =>
      val requestBody = Resource.fromInputStream(req.inputStream).byteArray
      captureValue("HTTP PUT Request from " + from + " to " + to -> ("PUT " + req.uri + "\n\n" + new String(requestBody)))
      client(url(baseUrl + req.uri).PUT.setBody(requestBody) > CaptureAndProxyResponse)

    case POST(req) =>
      val requestBody = Resource.fromInputStream(req.inputStream).byteArray
      captureValue("HTTP POST Request from " + from + " to " + to -> ("POST " + req.uri + "\n\n" + new String(requestBody)))
      client(url(baseUrl + req.uri).POST.setBody(requestBody) > CaptureAndProxyResponse)

    case DELETE(req) =>
      captureValue("HTTP DELETE Request from " + from + " to " + to -> ("DELETE " + req.uri))
      client(url(baseUrl + req.uri).DELETE > CaptureAndProxyResponse)

    case HEAD(req) =>
      captureValue("HTTP HEAD Request from " + from + " to " + to -> ("HEAD " + req.uri))
      client(url(baseUrl + req.uri).HEAD > CaptureAndProxyResponse)

    case _ =>
      Future(ResponseString("Not supported"))
  }

  private val server = netty.Server.http(listenPort).plan(plan)

  def start() {
    server.start()
  }

  def stop() {
    server.stop()
    client.shutdown()
  }

  object CaptureAndProxyResponse extends (Response => ResponseFunction[Any]) {
    def apply(response: Response): ResponseFunction[Any] = {
      val body = response.getResponseBodyAsBytes
      val contentType = Option(response.getContentType)
      val status = response.getStatusCode
      val statusText = response.getStatusText

      captureResponse(body, contentType.getOrElse("(No Content Type)"), status, statusText)

      Status(status) ~> ResponseBytes(body) ~> ContentType(contentType.getOrElse(""))
    }
    
    private def captureResponse(body: Array[Byte], contentType: String, statusCode: Int, statusText: String) = {
      val label = "HTTP Response (" + statusCode + " " + statusText +") from " + to + " to " + from
      
      if (contentType contains "json")
        captureValue(label -> new String(body))
      else if (contentType contains "xml")
        captureValue(label -> XML.loadString(new String(body)))
      else if (contentType contains "text")
        captureValue(label -> new String(body))
      else
        captureValue(label -> contentType)
    }    
  }
}

object RecordingHttpProxy {
  def listeningOnAnyLocalPort(remoteHost: String, remotePort: Int, from: String, to: String)(implicit executor: ExecutionContext) = RecordingHttpProxy(util.Port.any, remoteHost, remotePort, from, to)
}
