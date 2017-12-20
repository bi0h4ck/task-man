package us.diempham.taskman.database

import us.diempham.taskman.application.UserStorage.{Email, User, UserId}

trait UserDatabaseInterface extends DatabaseInterface[UserId, User] {
  def getUserbyEmail(email: Email): Option[User]
}
