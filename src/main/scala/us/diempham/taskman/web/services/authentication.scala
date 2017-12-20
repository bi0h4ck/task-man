package us.diempham.taskman.web.services

import java.util.UUID

object authentication {
  val generateToken: String = UUID.randomUUID().toString
}
