package us.diempham.taskman.application

import argonaut.CodecJson
import argonaut._, Argonaut._
import cats.effect.IO
import org.http4s.argonaut._

import scala.collection.mutable

object UserStorage {

  case class Id(value: String) extends AnyVal
  object Id{
    implicit def idEncodeJson: EncodeJson[Id] = jencode1[Id, String](id => id.value)
    implicit def idDecodeJson: DecodeJson[Id] = jdecode1[String, Id](idString => Id(idString))
  }

  case class Email(value: String) extends AnyVal
  object Email{
    implicit def EmailEncoding: EncodeJson[Email] = jencode1[Email, String](email => email.value)
    implicit def EmailDecoding: DecodeJson[Email] = jdecode1[String, Email](emailString => Email(emailString))
  }

  case class Password(value: String) extends AnyVal
  object Password{
    implicit def PasswordEncoding: EncodeJson[Password] = jencode1[Password, String](password => password.value)
    implicit def PasswordDecoding: DecodeJson[Password] = jdecode1[String, Password](passwordString => Password(passwordString))
  }

  case class User(id: Id, email: Email, password: Password)
  object User {
    implicit def UserCodecJson: CodecJson[User] = casecodec3(User.apply, User.unapply)("id", "email", "password")

    implicit val userDecoder = jsonOf[IO, User]
  }

  private val database: mutable.Map[Id, User] = mutable.Map.empty[Id, User]

  def createUser(user: User) = {
    database += (user.id -> User(user.id, user.email, user.password))
  }

  def deleteUser(id: Id) = {
    database -= id
  }

  def updateUser(id: Id)(f: User => User): Option[User] = {
    val result = database.get(id).map(f)
    result.map(createUser)
    result
  }

}
