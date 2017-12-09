package us.diempham.taskman.web.services

import argonaut.CodecJson
import argonaut._
import Argonaut._
import cats.effect.IO
import org.http4s.argonaut.jsonOf
import us.diempham.taskman.application.UserStorage.{Email, Id, Password}

object domain {
  case class CreateUserRequest(id: Id, email: Email, password: Password)

  object CreateUserRequest{
    implicit def createUserRequestCodecJson: CodecJson[CreateUserRequest] = casecodec3(CreateUserRequest.apply, CreateUserRequest.unapply)("id", "email", "password")
    implicit val userDecoder = jsonOf[IO, CreateUserRequest]
  }
}
