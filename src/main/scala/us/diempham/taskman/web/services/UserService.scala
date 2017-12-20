package us.diempham.taskman.web.services

import java.util.UUID

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.UserStorage
import us.diempham.taskman.application.UserStorage.{Password, User, UserId}
import us.diempham.taskman.database.{InMemoryTaskDatabase, InMemoryUserDatabase}
import us.diempham.taskman.web.services.domain.{CreateUserRequest, CreateUserRequestResponse, taskToTaskResponse}
import us.diempham.taskman.web.services.extractor.UserIdExtractor
import us.diempham.taskman.web.services.domain._

class UserService(userStorage: InMemoryUserDatabase, taskStorage: InMemoryTaskDatabase) {

  val USERS = "users"
  val TASKS = "tasks"
  val LOGIN = "login"

  val service = HttpService[IO] {
    case request@POST -> Root / USERS =>
      for {
        user <- request.as[CreateUserRequest]
        id = UserId(UUID.randomUUID())
        insertResult = userStorage.create(id, UserStorage.User(id, user.email, user.password))
        response <- Ok(userToUserResponse(insertResult))
      } yield response

    case GET -> Root / USERS / UserIdExtractor(userId) / TASKS =>
      Ok(taskStorage.getTasksForUser(userId).map(taskToTaskResponse))

    case request@POST -> Root / USERS / LOGIN =>
      for {
        login <- request.as[LoginRequest]
        maybeUser = userStorage.getUserbyEmail(login.email)
        verifiedUser = maybeUser.filter(checkPassword(_, login.password))
        response <- verifiedUser match {
          case None => BadRequest("Invalid email or password")
          case Some(user) => Ok(loginRequestToLoginResponse(user, Token(UUID.randomUUID().toString)))
        }
      } yield response

  }
      def userToUserResponse(user: User): CreateUserRequestResponse = CreateUserRequestResponse(user.id, user.email, user.password)

      def loginRequestToLoginResponse(user: User, token: Token): LoginResponse = LoginResponse(user, token)

      def checkPassword(givenUser: User, givenPassword: Password): Boolean = {
        givenUser.password == givenPassword
      }

}