package model.datastore

import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.driver.HsqldbDriver.simple._
import slick.session.Session
import com.google.inject.{Inject, Singleton}
import Schema._

case class DecisionDTO(code: String, id: String)

@Singleton
class DecisionDataStore @Inject()() extends SQLDataStore[DecisionDTO] {
  val table = Decisions.tableName

  Schema.createTables

  implicit def getEntityResult = GetResult(r => DecisionDTO(r.<<, r.<<))

  def insert(dto: DecisionDTO) = Decisions.insert(dto.code, dto.id)

}
