package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._

@Singleton
class AlternativeDataStore @Inject()() extends SQLDataStore[AlternativeDTO] {
  val table = Alternatives.tableName

  def insert(dto: AlternativeDTO)(implicit session: Session) = Alternatives.insert(dto)

  def findByDecisionId(id: String)(implicit session: Session) = Alternatives.filter(_.decisionId === id).to[Seq]

  def findById(id: String)(implicit session: Session) = Alternatives.filter(_.id === id).firstOption
}
