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
  val table = Criteria.tableName

  def insert(dto: CriteriaDTO)(implicit session: Session) = Criteria.insert(dto)

  def findById(id: UUID)(implicit session: Session) = Criteria.filter(_.id === id.bind).firstOption

  def findByDecisionId(id: UUID)(implicit session: Session) = Criteria.filter(_.decisionId === id.bind).to[Seq].asInstanceOf[Seq[CriteriaDTO]]
}
