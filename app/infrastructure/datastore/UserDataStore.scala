package infrastructure.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._
import java.util.UUID

@Singleton
class UserDataStore @Inject()() extends SQLDataStore[UserDTO] {
  def insert(dto: UserDTO)(implicit session: Session) = Users.insert(dto)

  def findById(id: UUID)(implicit session: Session) = Query(Users).filter(_.id === id.bind).firstOption

  def findByName(name: String)(implicit session: Session) = Query(Users).filter(_.name === name).firstOption

  def clear(implicit session: Session) { Query(Users).delete }
}
