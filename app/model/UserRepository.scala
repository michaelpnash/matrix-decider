package model

import com.google.inject.{Inject, Singleton}
import model.datastore.UserDataStore
import model.datastore.Schema.UserDTO
import scala.slick.session.Session
import java.util.UUID

@Singleton
class UserRepository @Inject()(userDataStore: UserDataStore, implicit val session: Session) {
  def save(user: User): User = {
    userDataStore.insert(UserDTO(user.id, user.name.toString))
    user
  }
  def findByName(name: String): Option[User] = userDataStore.findByName(name).map(dto => User(dto.name, dto.id))

  def clear = userDataStore.clear

  def findById(id: UUID): Option[User] = userDataStore.findById(id).map(dto => User(dto.name, dto.id))
}
