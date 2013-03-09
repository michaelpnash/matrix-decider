package model

import com.google.inject.{Inject, Singleton}
import model.datastore.UserDataStore
import model.datastore.Schema.UserDTO
import scala.slick.session.Session
import java.util.UUID

@Singleton
class UserRepository @Inject()(userDataStore: UserDataStore, implicit val session: Session) {
  def save(user: User): User = {
    userDataStore.insert(UserDTO(user.id.toString, user.name.toString))
    user
  }
  def findByName(name: String): Option[User] = userDataStore.findByName(name).map(dto => User(dto.name, UUID.fromString(dto.id)))

  def clear = userDataStore.clear
}
