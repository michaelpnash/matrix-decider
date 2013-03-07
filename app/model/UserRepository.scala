package model

import com.google.inject.{Inject, Singleton}
import model.datastore.UserDataStore
import model.datastore.Schema.UserDTO
import scala.slick.session.Session

@Singleton
class UserRepository @Inject()(userDataStore: UserDataStore, implicit val session: Session) {
  def save(user: User): User = {
    userDataStore.insert(UserDTO(user.id.toString, user.name.toString))
    user
  }
  def clear = userDataStore.clear
}
