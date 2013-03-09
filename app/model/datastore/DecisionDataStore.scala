package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._
import java.util.UUID

@Singleton
class DecisionDataStore @Inject()() extends SQLDataStore[DecisionDTO] {
  val table = Decisions.tableName

  def insert(dto: DecisionDTO)(implicit session: Session) = Decisions.insert(dto)

  def findById(id: UUID)(implicit session: Session) = Decisions.filter(_.id === id.bind).to[Seq].headOption

  def findForUser(id: UUID)(implicit session: Session) = Decisions.filter(_.userId === id.bind).elements.to[Seq].asInstanceOf[Seq[DecisionDTO]]
}
