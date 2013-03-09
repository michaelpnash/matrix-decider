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

  def update(dto: DecisionDTO)(implicit session: Session) = {
    require(findById(dto.id).isDefined, "No such decision")
    Decisions.filter(_.id === dto.id.bind).foreach { case entity: DecisionDTO =>
      (for(u <- Decisions if u.id === dto.id.bind) yield u) update (dto)
    }
  }
}
