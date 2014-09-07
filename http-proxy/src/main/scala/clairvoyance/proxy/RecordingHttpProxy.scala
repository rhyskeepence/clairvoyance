package clairvoyance.proxy

import unfiltered.netty
import unfiltered.util
import unfiltered.request._
import unfiltered.response._
import dispatch._
import com.ning.http.client.Response
import clairvoyance._
import scalax.io.Resource
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext

// TODO - deal with unsupported verbs better
// TODO - store binary/unknown response bodies - download to file and link to binary in captured value
case class RecordingHttpProxy(listenPort: Int, remoteHost: String, remotePort: Int, from: String, to: String, includePaths: Seq[Regex], excludePaths: Seq[Regex])(implicit executor: ExecutionContext) extends ProducesCapturedInputsAndOutputs {

  private val client = new Http
  private val baseUrl = "http://" + remoteHost + ":" + remotePort

  def captureIfRequestMatches(path: String): (Imports.KeyValue) => Unit = keyValue => {
    if (includePaths.forall(_.findFirstMatchIn(path).nonEmpty) && excludePaths.forall(_.findFirstMatchIn(path).isEmpty))
      captureValue(keyValue)
    else
      ()
  }

  private val plan = netty.future.Planify {
    case GET(req) =>
      val capture = captureIfRequestMatches(req.uri)
      capture("HTTP GET request from " + from + " to " + to -> ("GET " + requestToString(req)))
      client(buildRequestFrom(req).GET > ProxyResponseAndCapture(capture))

    case PUT(req) =>
      val capture = captureIfRequestMatches(req.uri)
      val requestBody = Resource.fromInputStream(req.inputStream).byteArray
      capture("HTTP PUT request from " + from + " to " + to -> ("PUT " + requestToString(req, Some(requestBody))))
      client(buildRequestFrom(req).PUT.setBody(requestBody) > ProxyResponseAndCapture(capture))

    case POST(req) =>
      val capture = captureIfRequestMatches(req.uri)
      val requestBody = Resource.fromInputStream(req.inputStream).byteArray
      capture("HTTP POST request from " + from + " to " + to -> ("POST " + requestToString(req, Some(requestBody))))
      client(buildRequestFrom(req).POST.setBody(requestBody) > ProxyResponseAndCapture(capture))

    case DELETE(req) =>
      val capture = captureIfRequestMatches(req.uri)
      capture("HTTP DELETE request from " + from + " to " + to -> ("DELETE " + requestToString(req)))
      client(buildRequestFrom(req).DELETE > ProxyResponseAndCapture(capture))

    case HEAD(req) =>
      val capture = captureIfRequestMatches(req.uri)
      capture("HTTP HEAD request from " + from + " to " + to -> ("HEAD " + requestToString(req)))
      client(buildRequestFrom(req).HEAD > ProxyResponseAndCapture(capture))

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

  def buildRequestFrom(req: HttpRequest[_]): Req = {
    val baseRequest = url(baseUrl + req.uri)
    req.headerNames.foldLeft(baseRequest) {
      (r, headerName) => r.setHeader(headerName, req.headers(headerName).mkString(", "))
    }
  }

  def requestToString(req: HttpRequest[_], body: Option[Array[Byte]] = None): String = {
    req.uri + "\n\n" +
      req.headerNames.map(name => name + ": " + req.headers(name).mkString(";")).mkString("\n") +
      body.map(b => "\n\n" + new String(b)).getOrElse("")
  }
  
  case class ProxyResponseAndCapture(capture: Imports.KeyValue => Unit) extends (Response => ResponseFunction[Any]) {
    def apply(response: Response): ResponseFunction[Any] = {
      val body = response.getResponseBodyAsBytes
      val status = response.getStatusCode

      captureResponse(response, body)

      Status(status) ~> HeadersFrom(response) ~> ResponseBytes(body)

    }
    
    private def captureResponse(response: Response, body: Array[Byte]) = {
      val label = "HTTP response (" + response.getStatusCode + " " + response.getStatusText +") from " + to + " to " + from

      Option(response.getContentType) match {
        case Some(contentType) =>
          if (contentType.contains("json") || contentType.contains("xml") || contentType.contains("text")) {
            capture(label -> responseToString(response, body))
          } else {
            capture(label -> responseToString(response))
          }

        case None =>
          capture(label -> responseToString(response, body))
      }
    }

    def responseToString(response: Response, body: Array[Byte] = Array()): String = {
        import scala.collection.JavaConversions._
        response.getHeaders.keySet().map(name => name + ": " + response.getHeader(name)).mkString("\n") +
        "\n\n" + new String(body)
    }
  }

  case class HeadersFrom(response: Response) extends Responder[Any] {
    import scala.collection.JavaConversions._
    def respond(res: HttpResponse[Any]) = {
      response.getHeaders.keySet.foreach {
        headerName => res.header(headerName, response.getHeader(headerName))
      }
    }
  }
}

object RecordingHttpProxy {
  def listeningOnAnyLocalPort(remoteHost: String, remotePort: Int, from: String, to: String, includePaths: Seq[Regex], excludePaths: Seq[Regex])(implicit executor: ExecutionContext) = RecordingHttpProxy(util.Port.any, remoteHost, remotePort, from, to, includePaths, excludePaths)
  def listeningOnAnyLocalPort(remoteHost: String, remotePort: Int, from: String, to: String, includePaths: Seq[Regex])(implicit executor: ExecutionContext) = RecordingHttpProxy(util.Port.any, remoteHost, remotePort, from, to, includePaths, Seq.empty)
  def listeningOnAnyLocalPort(remoteHost: String, remotePort: Int, from: String, to: String)(implicit executor: ExecutionContext) = RecordingHttpProxy(util.Port.any, remoteHost, remotePort, from, to, Seq.empty, Seq.empty)
}
