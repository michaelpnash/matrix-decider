package domain

import com.google.inject.{Inject, Singleton}
import infrastructure.datastore.UserDataStore
import infrastructure.datastore.Schema.UserDTO
import scala.slick.session.{Database, Session}
import java.util.UUID
import javax.sql.DataSource

@Singleton
class UserRepository @Inject()(userDataStore: UserDataStore, dataStore: DataSource) {

  val database = Database.forDataSource(dataStore)
  
  def save(user: User): User = {
    database withTransaction { implicit session: Session =>
      userDataStore.insert(UserDTO(user.id, user.name.toString))
    }
    user
  }
  def findByName(name: String): Option[User] = database withSession { implicit session => userDataStore.findByName(name).map(dto => User(dto.name, dto.id)) }

  def clear = database withTransaction { implicit session: Session => userDataStore.clear }

  def findById(id: UUID): Option[User] = database withSession { implicit session => userDataStore.findById(id).map(dto => User(dto.name, dto.id)) }
}
