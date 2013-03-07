package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._

@Singleton
class RankingDataStore @Inject()() extends SQLDataStore[RankingDTO] {
  val table = Rankings.tableName

  def insert(dto: RankingDTO)(implicit session: Session) = Rankings.insert(dto)

  def findByAlternativeId(id: String)(implicit session: Session) = Rankings.filter(_.alternativeId === id).to[Seq]

  def findByAlternativeIdAndCriteriaId(alternativeId: String, criteriaId: String)(implicit session: Session) =
    Rankings.filter(r => r.alternativeId === alternativeId && r.criteriaId === criteriaId).firstOption
}
