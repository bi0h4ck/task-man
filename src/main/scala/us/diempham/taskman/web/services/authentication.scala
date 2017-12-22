package us.diempham.taskman.web.services

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString
import org.http4s.{AuthedService, Credentials, Request}
import us.diempham.taskman.application.UserStorage.User
import us.diempham.taskman.database.InMemoryTokenDatabase
import us.diempham.taskman.web.services.domain.Token

class Authentication(tokenStorage: InMemoryTokenDatabase) {
  val BearerTokenString = CaseInsensitiveString("Bearer")

  val authUser: Kleisli[IO, Request[IO], Either[String, User]] = Kleisli { request =>
    val result = for {
      header <- request.headers.get(Authorization).toRight("Authorization header not found!")
      token <- validateAuthType(header)
      user <- validateToken(token).toRight("Invalid header")
    } yield user
    IO(result)
  }

  val onFailure: AuthedService[String, IO] = Kleisli(req => OptionT.liftF(Forbidden(req.authInfo)))

  val authorized: AuthMiddleware[IO, User] = AuthMiddleware(authUser, onFailure)

  def validateToken(givenToken: Token): Option[User] = tokenStorage.get(givenToken)

  def validateAuthType(header: Authorization.HeaderT): Either[String, Token] = {

    header.credentials match {
      case Credentials.Token(BearerTokenString, token) => Right(Token(token))
      case _ => Left("Invalid Authorization header")
    }
  }
}

