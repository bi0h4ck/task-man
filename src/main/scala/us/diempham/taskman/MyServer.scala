package us.diempham.taskman

import cats.effect.IO
import cats.implicits._
import fs2.Stream
import org.http4s.server.blaze._
import org.http4s.util.StreamApp
import org.http4s.util.ExitCode
import us.diempham.taskman.application.TaskStorage.{Task, TaskId}
import us.diempham.taskman.application.UserStorage.{User, UserId}
import us.diempham.taskman.database.InMemoryDatabase
import us.diempham.taskman.web.services.{TaskService, UserService}

object MyServer extends StreamApp[IO] {
  val userDatabase = new InMemoryDatabase[UserId, User]
  val taskDatabase = new InMemoryDatabase[TaskId, Task]
  val userService = new UserService(userDatabase)
  val taskService = new TaskService(taskDatabase)

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(taskService.service <+> userService.service, "/v1")
      .serve
}
