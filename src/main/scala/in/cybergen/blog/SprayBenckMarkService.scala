package in.cybergen.blog

import akka.actor.Actor
import org.cybergen.blog.Utility
import spray.can.Http
import spray.http.HttpMethods._
import spray.http.StatusCodes._
import spray.http._

import scala.concurrent.duration._

class SprayBenckMarkService extends Actor {
  import context.dispatcher
  import spray.http.Uri.Path._
  import spray.http.Uri._

  def jsonResponseEntity = HttpEntity(
    contentType = ContentTypes.`application/json`,
    string = "{\"status\":\"ok\"}")

  def fastPath: Http.FastPath = {
    case HttpRequest(GET, Uri(_, _, Slash(Segment("fast-ping", Path.Empty)), _, _), _, _, _) =>
      HttpResponse(entity = "FAST-PONG!")

    case HttpRequest(GET, Uri(_, _, Slash(Segment("fast-json", Path.Empty)), _, _), _, _, _) =>
      HttpResponse(entity = jsonResponseEntity)
  }

  def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self, fastPath = fastPath)
    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) => sender ! HttpResponse(entity = "\"status\":\"ok\"")
    case HttpRequest(GET, Uri.Path("/pingPong"), _, _, _) => sender ! HttpResponse(entity = Utility.getResponseString)
    case HttpRequest(GET, Uri.Path("/"), _, _, _) => sender ! HttpResponse(
      entity = HttpEntity(MediaTypes.`text/html`,
        <html>
          <body>
            <h1>Tiny <i>spray-can</i> benchmark server</h1>
            <p>Defined resources:</p>
            <ul>
              <li><a href="/ping">/ping</a></li>
              <li><a href="/fast-ping">/fast-ping</a></li>
              <li><a href="/json">/json</a></li>
              <li><a href="/fast-json">/fast-json</a></li>
              <li><a href="/stop">/stop</a></li>
            </ul>
          </body>
        </html>.toString()
      )
    )
    case HttpRequest(GET, Uri.Path("/json"), _, _, _) => sender ! HttpResponse(entity = jsonResponseEntity)

    case HttpRequest(GET, Uri.Path("/stop"), _, _, _) =>
      sender ! HttpResponse(entity = "Shutting down in 1 second ...")
      context.system.scheduler.scheduleOnce(1.second) { context.system.shutdown() }

    case _: HttpRequest => sender ! HttpResponse(NotFound, entity = "Unknown resource!")
  }
}