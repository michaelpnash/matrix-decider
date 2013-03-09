package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._
import java.util.UUID

@Singleton
class UserDataStore @Inject()() extends SQLDataStore[UserDTO] {
  val table = Users.tableName

  def insert(dto: UserDTO)(implicit session: Session) = Users.insert(dto)

  def findById(id: UUID)(implicit session: Session) = Users.filter(_.id === id.bind).elements.toSet.headOption.asInstanceOf[Option[UserDTO]]

  def findByName(name: String)(implicit session: Session) = Users.filter(_.name === name).to[Seq].headOption.asInstanceOf[Option[UserDTO]]
}
