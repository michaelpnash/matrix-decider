package infrastructure.datastore

import scala.slick.driver.HsqldbDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation

import slick.session.Session
import collection.mutable.ListBuffer
import java.util.UUID

object Schema {

  case class UserDTO(id: UUID, name: String)

   object Users extends Table[UserDTO]("USERS") {
     def id = column[UUID]("ID", O.PrimaryKey)
     def name = column[String]("NAME")
     def idx = index("idx_username", (name), unique = true)
     def * = id ~ name <> (UserDTO, UserDTO.unapply _)
   }

  case class DecisionDTO(user: UUID, name: String, id: UUID)

  object Decisions extends Table[DecisionDTO]("DECISIONS") {
    def userId = column[UUID]("USER_ID")
    def name = column[String]("NAME")
    def id = column[UUID]("ID", O.PrimaryKey)

    def owner = foreignKey("DECISION_USER", userId, Users)(_.id)

    def * = userId ~ name ~ id <> (DecisionDTO, DecisionDTO.unapply _)
  }

  case class CriteriaDTO(name: String, importance: Int, decisionId: UUID, id: UUID)

  object Criteria extends Table[CriteriaDTO]("CRITERIA") {
    def name = column[String]("NAME")
    def importance = column[Int]("IMPORTANCE")
    def decisionId = column[UUID]("DECISION_ID")
    def id = column[UUID]("ID", O.PrimaryKey)
    def idx = index("idx_crit_name", (name, decisionId), unique = true)
    def decision = foreignKey("CRIT_DECISION", decisionId, Decisions)(_.id)
    def * = name ~ importance ~ decisionId ~ id <> (CriteriaDTO, CriteriaDTO.unapply _)
  }

  case class AlternativeDTO(name: String, decisionId: UUID, id: UUID)

  object Alternatives extends Table[AlternativeDTO]("ALTERNATIVES") {
    def name = column[String]("NAME")
    def decisionId = column[UUID]("DECISION_ID")
    def id = column[UUID]("ID", O.PrimaryKey)
    def idx = index("idx_alt_name", (name, decisionId), unique = true)
    def decision = foreignKey("ALT_DECISION", decisionId, Decisions)(_.id)
    def * = name ~ decisionId ~ id <> (AlternativeDTO, AlternativeDTO.unapply _)
  }

  case class RankingDTO(criteriaId: UUID, alternativeId: UUID, rank: Int)

  object Rankings extends Table[RankingDTO]("RANKINGS") {
    def criteriaId = column[UUID]("CRITERIA_ID")
    def alternativeId = column[UUID]("ALTERNATIVE_ID")
    def rank = column[Int]("RANK")
    def rk = primaryKey("RANK_KEY", (criteriaId, alternativeId))
    def * = criteriaId ~ alternativeId ~ rank <> (RankingDTO, RankingDTO.unapply _)
  }

  def createTables(implicit session: Session) {
    val names = ListBuffer[String]()
    val meta = session.metaData.asInstanceOf[org.hsqldb.jdbc.JDBCDatabaseMetaData]
    val tables = meta.getTables(null, null, null, null)
    while (tables.next()) {
      val table = tables.getString("TABLE_NAME")
      names.append(table)
    }

    val tableNames = names.toList
    try {
      if (!tableNames.contains(Users.tableName)) Users.ddl.create
      if (!tableNames.contains(Decisions.tableName)) Decisions.ddl.create
      if (!tableNames.contains(Criteria.tableName)) Criteria.ddl.create
      if (!tableNames.contains(Alternatives.tableName)) Alternatives.ddl.create
      if (!tableNames.contains(Rankings.tableName)) Rankings.ddl.create
    } catch {
      case ex: Exception => throw new RuntimeException("Unable to create tables for database " + session.metaData.getURL(), ex)
    }
  }
}
