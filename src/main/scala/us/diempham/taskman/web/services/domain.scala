package us.diempham.taskman.web.services

import java.time.Instant
import argonaut.CodecJson
import argonaut._
import Argonaut._
import cats.effect.IO
import org.http4s.argonaut.jsonOf
import us.diempham.taskman.application.TaskStorage._
import us.diempham.taskman.application.{TaskStorage, UserStorage}
import us.diempham.taskman.application.TaskStorage.{Description, IsCompleted, TaskId, Title}
import us.diempham.taskman.application.UserStorage.{Email, Password}

object domain {
  case class CreateUserRequest(id: UserStorage.UserId,
                               email: Email,
                               password: Password)
  object CreateUserRequest {
    implicit def createUserRequestCodecJson: CodecJson[CreateUserRequest] =
      casecodec3(CreateUserRequest.apply, CreateUserRequest.unapply)("id", "email", "password")
    implicit val userDecoder = jsonOf[IO, CreateUserRequest]
  }

  case class CreateTaskRequest(taskId: TaskId,
                               userId: TaskStorage.UserId,
                               title: Title,
                               description: Description,
                               isCompleted: IsCompleted,
                               createdOn: Instant)
  object CreateTaskRequest {
    implicit def createTaskRequestCodecJson: CodecJson[CreateTaskRequest] =
      casecodec6(CreateTaskRequest.apply, CreateTaskRequest.unapply)("taskId", "userId", "title", "description", "isCompleted", "createdOn")
    implicit val taskDecoder = jsonOf[IO, CreateTaskRequest]
  }
}
