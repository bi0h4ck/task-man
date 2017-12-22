package us.diempham.taskman.web.services

import java.util.UUID
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.UserStorage
import us.diempham.taskman.application.UserStorage.{Password, User, UserId}
import us.diempham.taskman.database.{InMemoryTaskDatabase, InMemoryTokenDatabase, InMemoryUserDatabase}
import us.diempham.taskman.web.services.domain.{CreateUserRequest, CreateUserRequestResponse, _}
import us.diempham.taskman.web.services.extractor.UserIdExtractor

class UserService(userStorage: InMemoryUserDatabase, taskStorage: InMemoryTaskDatabase, tokenStorage: InMemoryTokenDatabase) {

  val USERS = "users"
  val TASKS = "tasks"
  val LOGIN = "login"

  val authedService: AuthedService[User, IO] =
    AuthedService {
      case GET -> Root / USERS / UserIdExtractor(userId) / TASKS as _ =>
        Ok(taskStorage.getTasksForUser(userId).map(taskToTaskResponse))
    }

  val service = HttpService[IO]{
    case request @ POST -> Root / USERS =>
      for {
        user <- request.as[CreateUserRequest]
        id = UserId(UUID.randomUUID())
        insertResult = userStorage.create(id, UserStorage.User(id, user.email, user.password))
        response <- Ok(userToUserResponse(insertResult))
      } yield response

    case request @ POST -> Root / USERS / LOGIN =>
      for {
        login <- request.as[LoginRequest]
        maybeUser = userStorage.getUserbyEmail(login.email)
        verifiedUser = maybeUser.filter(checkPassword(_, login.password))
        response <- verifiedUser match {
          case None => BadRequest("Invalid email or password")
          case Some(user) => {
            val token = Token(UUID.randomUUID().toString)
            tokenStorage.create(token, user)
            Ok(loginRequestToLoginResponse(user, token))
          }
        }
      } yield response

  }
      def userToUserResponse(user: User): CreateUserRequestResponse = CreateUserRequestResponse(user.id, user.email, user.password)

      def loginRequestToLoginResponse(user: User, token: Token): LoginResponse = LoginResponse(user, token)

      def checkPassword(givenUser: User, givenPassword: Password): Boolean = {
        givenUser.password == givenPassword
      }

      def validateToken(givenToken: Token): Option[User] = tokenStorage.get(givenToken)



}