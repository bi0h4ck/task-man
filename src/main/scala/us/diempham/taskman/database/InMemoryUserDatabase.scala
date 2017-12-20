package us.diempham.taskman.database

import us.diempham.taskman.application.UserStorage.{Email, User, UserId}


class InMemoryUserDatabase extends InMemoryDatabase[UserId, User] with UserDatabaseInterface {
  def getUserbyEmail(givenEmail: Email): Option[User] = {
    database.find{ case (_, user) => user.email == givenEmail }.map(_._2)
  }

}
