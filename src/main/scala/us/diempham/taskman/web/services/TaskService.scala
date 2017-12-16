package us.diempham.taskman.web.services

import cats.effect.IO
import org.http4s._
import org.http4s.dsl.io._
import us.diempham.taskman.application.TaskStorage
import us.diempham.taskman.application.TaskStorage.{TaskId, Task}
import us.diempham.taskman.database.InMemoryDatabase
import us.diempham.taskman.web.services.domain.CreateTaskRequest

class TaskService(taskStorage: InMemoryDatabase[TaskId, Task]) {

 val service = HttpService[IO] {
   case request @ POST -> Root / "tasks" =>
     for {
       task <- request.as[CreateTaskRequest]
       insertTask = taskStorage.create(task.taskId, TaskStorage.Task(task.taskId, task.userId, task.title, task.description, task.isCompleted, task.createdOn))
       response <- Ok(())
     } yield response


 }
}
