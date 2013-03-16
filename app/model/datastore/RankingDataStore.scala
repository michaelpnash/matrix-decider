package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._
import java.util.UUID

@Singleton
class RankingDataStore @Inject()() extends SQLDataStore[RankingDTO] {
  val table = Rankings.tableName

  def insert(dto: RankingDTO)(implicit session: Session) = Rankings.insert(dto)

  def findByAlternativeId(id: UUID)(implicit session: Session) = Query(Rankings).filter(_.alternativeId === id.bind).to[Seq]

  def findByAlternativeIdAndCriteriaId(alternativeId: UUID, criteriaId: UUID)(implicit session: Session) =
    Query(Rankings).filter(r => r.alternativeId === alternativeId.bind && r.criteriaId === criteriaId.bind).firstOption

  def updateRanking(alternativeId: UUID, criteriaId: UUID, i: Int)(implicit session: Session) =  Query(Rankings).filter(r => r.alternativeId === alternativeId.bind && r.criteriaId === criteriaId.bind).foreach { case entity: RankingDTO =>
       (for(u <- Rankings) yield u) update (entity.copy(rank = i))
    }

  def clear(implicit session: Session) { Query(Rankings).delete }
}
