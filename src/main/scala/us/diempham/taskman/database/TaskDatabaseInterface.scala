package us.diempham.taskman.database

import us.diempham.taskman.application.TaskStorage.{Task, TaskId}
import us.diempham.taskman.application.UserStorage.UserId

trait TaskDatabaseInterface extends DatabaseInterface[TaskId, Task] {
  def getTasksForUser(userId: UserId): List[Task]
}
