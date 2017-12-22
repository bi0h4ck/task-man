package us.diempham.taskman

import cats.effect.IO
import cats.implicits._
import fs2.Stream
import org.http4s.server.blaze._
import org.http4s.util.StreamApp
import org.http4s.util.ExitCode
import us.diempham.taskman.database.{InMemoryTaskDatabase, InMemoryTokenDatabase, InMemoryUserDatabase}
import us.diempham.taskman.web.services.{Authentication, TaskService, UserService}

object MyServer extends StreamApp[IO] {
  val userDatabase = new InMemoryUserDatabase
  val taskDatabase = new InMemoryTaskDatabase
  val tokenDatabase = new InMemoryTokenDatabase
  val authentication = new Authentication(tokenDatabase)
  val userService = new UserService(userDatabase, taskDatabase, tokenDatabase)
  val taskService = new TaskService(taskDatabase, userDatabase)

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {
    val authorizedServices =
      taskService.authedService <+>
      userService.authedService

    val noAuthServices = userService.service

    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(noAuthServices <+> authentication.authorized(authorizedServices), "/v1")
      .serve

  }
}
