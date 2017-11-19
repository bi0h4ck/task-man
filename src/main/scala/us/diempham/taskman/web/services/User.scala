package us.diempham.taskman.web.services

import scala.collection.mutable

object User {
  case class User(id: String, email: String, password: String)

  val database: mutable.Map[String, User] = mutable.Map.empty[String, User]

  def createUser(id: String, email: String, password: String) = {
    database += (id -> User(id, email, password))
  }

  def deleteUser(id: String) = {
    database -= id
  }

  

}
