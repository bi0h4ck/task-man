package us.diempham.taskman.web.services

import java.util.UUID

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.UserStorage
import us.diempham.taskman.application.UserStorage.{User, UserId}
import us.diempham.taskman.database.{InMemoryDatabase, InMemoryTaskDatabase}
import us.diempham.taskman.web.services.domain.{CreateUserRequest, CreateUserRequestResponse, taskToTaskResponse}
import us.diempham.taskman.web.services.extractor.UserIdExtractor
import us.diempham.taskman.web.services.domain._

class UserService(userStorage: InMemoryDatabase[UserId, User], taskStorage: InMemoryTaskDatabase) {

  val USERS = "users"
  val TASKS = "tasks"
  val service = HttpService[IO] {
    case request @ POST -> Root / USERS =>
      for {
        user <- request.as[CreateUserRequest]
        id = UserId(UUID.randomUUID())
        insertResult = userStorage.create(id, UserStorage.User(id, user.email, user.password))
        response <- Ok(userToUserResponse(insertResult))
      } yield response

    case GET -> Root / USERS / UserIdExtractor(userId) / TASKS =>
      Ok(taskStorage.getTasksForUser(userId).map(taskToTaskResponse))
  }

  def userToUserResponse(user: User): CreateUserRequestResponse = CreateUserRequestResponse(user.id, user.email, user.password)

}
