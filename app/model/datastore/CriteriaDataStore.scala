package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._

@Singleton
class CriteriaDataStore @Inject()() extends SQLDataStore[CriteriaDTO] {
  val table = Criteria.tableName

  def insert(dto: CriteriaDTO)(implicit session: Session) = Criteria.insert(dto)

  def findById(id: String)(implicit session: Session) = Criteria.filter(_.id === id).firstOption

  def findByDecisionId(id: String)(implicit session: Session) = Criteria.filter(_.decisionId === id).to[Seq]
}
