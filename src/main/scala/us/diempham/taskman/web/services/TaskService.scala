package us.diempham.taskman.web.services

import java.util.UUID

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.TaskStorage
import us.diempham.taskman.application.TaskStorage.{IsCompleted, Task, TaskId}
import us.diempham.taskman.database.InMemoryDatabase
import us.diempham.taskman.web.services.domain.{CreateTaskRequest, TaskResponse}
import us.diempham.taskman.web.services.extractor.TaskIdExtractor

class TaskService(taskStorage: InMemoryDatabase[TaskId, Task]) {
  val TASKS = "tasks"

  val service = HttpService[IO] {
    case request @ POST -> Root / TASKS =>
      for {
        task <- request.as[CreateTaskRequest]
        taskId = TaskId(UUID.randomUUID())
        insertTask = taskStorage.create(taskId, TaskStorage.Task(taskId, task.userId, task.title, task.description, task.isCompleted, task.createdOn))
        response <- Ok(taskToTaskResponse(insertTask))
      } yield response

    case PUT -> Root / TASKS / TaskIdExtractor(taskId) / "completed" =>
        taskStorage.update(taskId)(task =>  task.copy(isCompleted = IsCompleted(true))) match {
          case None => NotFound()
          case Some(updatedTask) => Ok(taskToTaskResponse(updatedTask))
        }

    case GET -> Root / TASKS / TaskIdExtractor(taskId) =>
      taskStorage.get(taskId) match {
        case None => NotFound()
        case Some(updatedTask) => Ok(taskToTaskResponse(updatedTask))
      }
  }

  def taskToTaskResponse(task: Task): TaskResponse = TaskResponse(task.taskId, task.userId, task.title, task.description, task.isCompleted, task.createdOn)
}
