package us.diempham.taskman.web.services

import java.util.UUID
import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.TaskStorage
import us.diempham.taskman.application.TaskStorage.{IsCompleted, Task, TaskId}
import us.diempham.taskman.application.UserStorage.User
import us.diempham.taskman.database.{InMemoryTaskDatabase, InMemoryUserDatabase}
import us.diempham.taskman.web.services.domain.{CreateTaskRequest, taskToTaskResponse}
import us.diempham.taskman.web.services.extractor.{TaskIdExtractor}

class TaskService(taskStorage: InMemoryTaskDatabase, userStorage: InMemoryUserDatabase) {
  val TASKS = "tasks"

  val authedService: AuthedService[User, IO] =
    AuthedService[User, IO] {
      case request @ POST -> Root / TASKS as _ =>
        for {
          task <- request.req.as[CreateTaskRequest]
          response <- maybeInsertTask(task) match {
            case None => NotFound("User doesn't exist!")
            case Some(task) => Ok(taskToTaskResponse(task))
          }
        } yield response

      case PUT -> Root / TASKS / TaskIdExtractor(taskId) / "completed" as _ =>
        taskStorage.update(taskId)(task =>  task.copy(isCompleted = IsCompleted(true))) match {
          case None => NotFound()
          case Some(updatedTask) => Ok(taskToTaskResponse(updatedTask))
        }

      case GET -> Root / TASKS / TaskIdExtractor(taskId) as _ =>
        taskStorage.get(taskId) match {
          case None => NotFound()
          case Some(updatedTask) => Ok(taskToTaskResponse(updatedTask))
        }
    }

  def maybeInsertTask(task: CreateTaskRequest): Option[Task] = {
    val id = TaskId(UUID.randomUUID())
    val getUserResult = userStorage.get(task.userId)
    getUserResult.map(_ => taskStorage.create(id, TaskStorage.Task(id, task.userId, task.title, task.description, task.isCompleted, task.createdOn)))
  }
}
