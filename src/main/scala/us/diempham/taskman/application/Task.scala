package us.diempham.taskman.application

import java.time.Instant
import us.diempham.taskman.application.UserStorage.UserId

object Task {
  case class TaskId(value: String) extends AnyVal

  case class Title(value: String) extends AnyVal

  case class Description(value: String) extends AnyVal

  case class IsCompleted(value: Boolean)

  case class Task(taskid: TaskId, userId: UserId, title: Title, description: Description, isCompleted: IsCompleted, createdOn: Instant)

}
