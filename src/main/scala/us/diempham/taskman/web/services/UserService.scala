package us.diempham.taskman.web.services

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.UserStorage
import us.diempham.taskman.application.UserStorage.{User, UserId}
import us.diempham.taskman.database.InMemoryDatabase
import us.diempham.taskman.web.services.domain.CreateUserRequest

class UserService(userStorage: InMemoryDatabase[UserId, User]) {

  val service = HttpService[IO] {
    case request @ POST -> Root / "users" =>
      for {
        user <- request.as[CreateUserRequest]
        insertResult = userStorage.create(user.id, UserStorage.User(user.id, user.email, user.password))
        response <- Ok(())
      } yield response
  }
}
