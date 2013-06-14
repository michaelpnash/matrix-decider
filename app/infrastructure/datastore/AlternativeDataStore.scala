package infrastructure.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._
import java.util.UUID

@Singleton
class AlternativeDataStore @Inject()() extends SQLDataStore[AlternativeDTO] {

  def insert(dto: AlternativeDTO)(implicit session: Session) = Alternatives.insert(dto)

  def findByDecisionId(id: UUID)(implicit session: Session) = Query(Alternatives).filter(_.decisionId === id.bind).to[Seq]

  def findById(id: UUID)(implicit session: Session) = Query(Alternatives).filter(_.id === id.bind).firstOption

  def clear(implicit session: Session) { Query(Alternatives).delete }
}
