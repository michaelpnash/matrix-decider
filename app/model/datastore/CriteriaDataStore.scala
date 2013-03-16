package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._
import java.util.UUID

@Singleton
class CriteriaDataStore @Inject()() extends SQLDataStore[CriteriaDTO] {
  def insert(dto: CriteriaDTO)(implicit session: Session) = Criteria.insert(dto)

  def findById(id: UUID)(implicit session: Session) = Query(Criteria).filter(_.id === id.bind).firstOption

  def findByDecisionId(id: UUID)(implicit session: Session) = Query(Criteria).filter(_.decisionId === id.bind).to[Seq]

  def updateImportance(id: UUID, i: Int)(implicit session: Session) =  Query(Criteria).filter(_.id === id.bind).foreach { case entity: CriteriaDTO =>
       (for(u <- Criteria if u.id === id.bind) yield u) update (entity.copy(importance = i))
    }

  def clear(implicit session: Session) { Query(Criteria).delete }
}
