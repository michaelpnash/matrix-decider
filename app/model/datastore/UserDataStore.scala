package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._

@Singleton
class UserDataStore @Inject()(implicit val session: Session) extends SQLDataStore[UserDTO] {
  val table = Users.tableName

  Schema.createTables

  def insert(dto: UserDTO) = Users.insert(dto)

  def findById(id: String) = Users.filter(_.id === id).elements.toSet.headOption

  def findByName(name: String) = Users.filter(_.name === name).to[Seq].headOption.asInstanceOf[Option[UserDTO]]
}
