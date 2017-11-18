package us.diempham.taskman

import cats.effect.IO
import fs2.Stream
import org.http4s.server.blaze._
import org.http4s.util.StreamApp
import org.http4s.util.ExitCode
import us.diempham.taskman.web.services.UserService


object MyServer extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(UserService.service, "/v1")
      .serve
}
