package us.diempham.taskman.database

import us.diempham.taskman.application.UserStorage.User
import us.diempham.taskman.web.services.domain.Token

class InMemoryTokenDatabase extends InMemoryDatabase [Token, User] {
  def getUserbyToken(token: Token): Option[User] = database.get(token)
}
