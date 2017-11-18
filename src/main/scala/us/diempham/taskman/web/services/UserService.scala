package us.diempham.taskman.web.services

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

object UserService {
  val service = HttpService[IO] {
    case POST -> Root / "users" =>
      IO(Response(Status.Ok))
  }
}
