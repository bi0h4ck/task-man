package us.diempham.taskman.web.services

import java.time.Instant

import argonaut.CodecJson
import argonaut._
import Argonaut._
import cats.effect.IO
import org.http4s.argonaut._
import us.diempham.taskman.application.TaskStorage._
import us.diempham.taskman.application.TaskStorage.{Description, IsCompleted, TaskId, Title}
import us.diempham.taskman.application.UserStorage.{Email, Password, User, UserId}

object domain {
  case class CreateUserRequest(email: Email,
                               password: Password)
  object CreateUserRequest {
    implicit def createUserRequestCodecJson: CodecJson[CreateUserRequest] =
      casecodec2(CreateUserRequest.apply, CreateUserRequest.unapply)("email", "password")
    implicit val userDecoder = jsonOf[IO, CreateUserRequest]
  }

  case class CreateUserRequestResponse(userId: UserId,
                                       email: Email,
                                       password: Password)
  object CreateUserRequestResponse {
    implicit def createUserResponseCodecJson: CodecJson[CreateUserRequestResponse] =
      casecodec3(CreateUserRequestResponse.apply, CreateUserRequestResponse.unapply)("userId", "email", "password")
    implicit val createUserResponseEncoder = jsonEncoderOf[IO, CreateUserRequestResponse]
  }

  case class CreateTaskRequest(userId: UserId,
                               title: Title,
                               description: Description,
                               isCompleted: IsCompleted,
                               createdOn: Instant)
  object CreateTaskRequest {
    implicit def createTaskRequestCodecJson: CodecJson[CreateTaskRequest] =
      casecodec5(CreateTaskRequest.apply, CreateTaskRequest.unapply)("userId", "title", "description", "isCompleted", "createdOn")
    implicit val taskDecoder = jsonOf[IO, CreateTaskRequest]
  }

  case class TaskResponse(taskId: TaskId,
                          userId: UserId,
                          title: Title,
                          description: Description,
                          isCompleted: IsCompleted,
                          createdOn: Instant)
  object TaskResponse {
    implicit def taskResponse: CodecJson[TaskResponse] =
      casecodec6(TaskResponse.apply, TaskResponse.unapply)("taskId", "userId", "title", "description", "isCompleted", "createdOn")
    implicit val taskResponseDecoder = jsonOf[IO, TaskResponse]
    implicit val taskResponseEncoder = jsonEncoderOf[IO, TaskResponse]
    implicit val listOfTaskResponseEncoder = jsonEncoderOf[IO, List[TaskResponse]]
  }

  def taskToTaskResponse(task: Task): TaskResponse = TaskResponse(task.taskId, task.userId, task.title, task.description, task.isCompleted, task.createdOn)

  case class LoginRequest(email: Email,
                          password: Password)
  object LoginRequest {
    implicit def loginRequest: CodecJson[LoginRequest] = casecodec2(LoginRequest.apply, LoginRequest.unapply)("email", "password")
    implicit val loginRequestDecoder = jsonOf[IO, LoginRequest]
  }

  case class Token(value: String)
  object Token{
    implicit val tokenEncoder: EncodeJson[Token] = jencode1[Token, String](token => token.value)
    implicit val tokenDecoder: DecodeJson[Token] = jdecode1[String, Token](tokenString => Token(tokenString))
  }

  case class LoginResponse(user: User, token: Token)
  object LoginResponse {
    implicit def loginResponse: CodecJson[LoginResponse] = casecodec2(LoginResponse.apply, LoginResponse.unapply)("user", "token")
    implicit val loginResponseEncoder = jsonEncoderOf[IO, LoginResponse]
  }
}
