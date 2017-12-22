package us.diempham.taskman.web.services

import us.diempham.taskman.application.TaskStorage.TaskId
import us.diempham.taskman.application.UserStorage.UserId

import scala.util.Try

object extractor {
  object TaskIdExtractor {
    def unapply(s: String): Option[TaskId] =
      Try(TaskId(java.util.UUID.fromString(s))).toOption
  }

  object UserIdExtractor {
    def unapply(s: String): Option[UserId] =
      Try(UserId(java.util.UUID.fromString(s))).toOption
  }
}
