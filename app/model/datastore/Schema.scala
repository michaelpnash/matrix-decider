package model.datastore

import scala.slick.driver.HsqldbDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation

import slick.session.Session
import collection.mutable.ListBuffer

object Schema {

  case class UserDTO(id: String, name: String)

   object Users extends Table[UserDTO]("USERS") {
     def id = column[String]("ID", O.PrimaryKey)
     def name = column[String]("NAME")
     def idx = index("idx_username", (name), unique = true)
     def * = id ~ name <> (UserDTO, UserDTO.unapply _)
   }

  case class DecisionDTO(user: String, alternatives: String, criteria: String, id: String)

  //user: String, alternatives: String, criteria: String, id: String
  object Decisions extends Table[DecisionDTO]("DECISIONS") {
    def userId = column[String]("USER_ID")
    def alternatives = column[String]("ALTERNATIVES")
    def criteria = column[String]("CRITERIA")
    def id = column[String]("ID", O.PrimaryKey)

    def owner = foreignKey("DEC", userId, Users)(_.id)

    def * = userId ~ alternatives ~ criteria ~ id <> (DecisionDTO, DecisionDTO.unapply _)
  }

  case class CriteriaDTO(name: String, importance: Int, id: String)

  object Criteria extends Table[CriteriaDTO]("CRITERIA") {
    def name = column[String]("NAME")
    def importance = column[Int]("IMPORTANCE")
    def id = column[String]("ID", O.PrimaryKey)
    def * = name ~ importance ~ id <> (CriteriaDTO, CriteriaDTO.unapply _)
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
      if (!tableNames.contains(Criteria.tableName)) Criteria.ddl.create
      if (!tableNames.contains(Decisions.tableName)) Decisions.ddl.create
    } catch {
      case ex: Exception => throw new RuntimeException("Unable to create tables for database " + session.metaData.getURL(), ex)
    }
  }
}
