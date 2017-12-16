package us.diempham.taskman.web.services

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.TaskStorage
import us.diempham.taskman.application.TaskStorage.{IsCompleted, Task, TaskId}
import us.diempham.taskman.database.InMemoryDatabase
import us.diempham.taskman.web.services.domain.{CreateTaskRequest}

class TaskService(taskStorage: InMemoryDatabase[TaskId, Task]) {
  val TASKS = "tasks"

  val service = HttpService[IO] {
    case request @ POST -> Root / TASKS =>
      for {
        task <- request.as[CreateTaskRequest]
        insertTask = taskStorage.create(task.taskId, TaskStorage.Task(task.taskId, task.userId, task.title, task.description, task.isCompleted, task.createdOn))
        response <- Ok(())
      } yield response

    case PUT -> Root / TASKS / taskId / "completed" =>
        taskStorage.update(TaskId(taskId))(task =>  task.copy(isCompleted = IsCompleted(true))) match {
          case None => NotFound()
          case Some(_) => Ok(())
        }
  }
}
