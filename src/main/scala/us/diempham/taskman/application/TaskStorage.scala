package us.diempham.taskman.application

import argonaut._
import Argonaut._
import java.time.Instant
import java.util.UUID

import org.http4s.argonaut._
import argonaut.CodecJson
import cats.effect.IO
import us.diempham.taskman.application.UserStorage.UserId

object TaskStorage {
  case class TaskId(value: UUID) extends AnyVal

  object TaskId{
    implicit def taskIdEncodeJson: EncodeJson[TaskId] = jencode1[TaskId, UUID](id => id.value)
    implicit def taskIdDecodeJson: DecodeJson[TaskId] = jdecode1[UUID, TaskId](id => TaskId(id))
  }

  case class Title(value: String) extends AnyVal

  object Title{
    implicit def titleEncodeJson: EncodeJson[Title] = jencode1[Title, String](title => title.value)
    implicit def titleDecodeJson: DecodeJson[Title] = jdecode1[String, Title](title => Title(title))
  }

  case class Description(value: String) extends AnyVal

  object Description{
    implicit def descriptionEncodeJson: EncodeJson[Description] = jencode1[Description, String](description => description.value)
    implicit def descriptionDecodeJson: DecodeJson[Description] = jdecode1[String, Description](description => Description(description))
  }

  case class IsCompleted(value: Boolean) extends AnyVal

  object IsCompleted{
    implicit def isCompletedEncodeJson: EncodeJson[IsCompleted] = jencode1[IsCompleted, Boolean](isCompleted => isCompleted.value)
    implicit def isCompletedDecodeJson: DecodeJson[IsCompleted] = jdecode1[Boolean, IsCompleted](isCompleted => IsCompleted(isCompleted))
  }

    implicit def instantEncodeJson: EncodeJson[Instant] = jencode1[Instant, String](time => time.toString())
    implicit def instantDecodeJson: DecodeJson[Instant] = jdecode1[String, Instant](timeString => Instant.parse(timeString))

  case class Task(taskId: TaskId,
                  userId: UserId,
                  title: Title,
                  description: Description,
                  isCompleted: IsCompleted,
                  createdOn: Instant)

  object Task{
    implicit def taskCodecJson: CodecJson[Task] = casecodec6(Task.apply, Task.unapply)("taskId", "userId", "title", "description", "isCompleted", "createdOn")
    implicit val taskDecoder = jsonOf[IO, Task]
  }

}
