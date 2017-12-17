package us.diempham.taskman.web.services

import java.util.UUID

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.UserStorage
import us.diempham.taskman.application.UserStorage.{User, UserId}
import us.diempham.taskman.database.InMemoryDatabase
import us.diempham.taskman.web.services.domain.{CreateUserRequest, CreateUserRequestResponse}

class UserService(userStorage: InMemoryDatabase[UserId, User]) {

  val service = HttpService[IO] {
    case request @ POST -> Root / "users" =>
      for {
        user <- request.as[CreateUserRequest]
        id = UserId(UUID.randomUUID())
        insertResult = userStorage.create(id, UserStorage.User(id, user.email, user.password))
        response <- Ok(userToUserResponse(insertResult))
      } yield response
  }

  def userToUserResponse(user: User): CreateUserRequestResponse = CreateUserRequestResponse(user.id, user.email, user.password)
}
