package us.diempham.taskman.database

import us.diempham.taskman.application.TaskStorage.{Task, TaskId}
import us.diempham.taskman.application.UserStorage.UserId

class InMemoryTaskDatabase extends InMemoryDatabase[TaskId, Task] with TaskDatabaseInterface {
  def getTasksForUser(userId: UserId): List[Task] = {
    database.filter { case (_, task) => task.userId == userId}.toList.map(_._2)
  }
}
