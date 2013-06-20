package domain

import com.google.inject.{Inject, Singleton}
import infrastructure.datastore.UserDataStore
import infrastructure.datastore.Schema.UserDTO
import scala.slick.session.{Database, Session}
import java.util.UUID

@Singleton
class UserRepository @Inject()(userDataStore: UserDataStore, database: Database) {

  implicit val session: Session = database.createSession

  def save(user: User): User = {
    userDataStore.insert(UserDTO(user.id, user.name.toString))
    user
  }
  def findByName(name: String): Option[User] = userDataStore.findByName(name).map(dto => User(dto.name, dto.id))

  def clear = userDataStore.clear

  def findById(id: UUID): Option[User] = userDataStore.findById(id).map(dto => User(dto.name, dto.id))
}
