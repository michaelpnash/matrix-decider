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
     //def idx = index("idx_username", (name), unique = true)
     def * = id ~ name <> (UserDTO, UserDTO.unapply _)

     //def dec = foreignKey("USR", id, Decisions)(_.userId)
   }

  case class DecisionDTO(user: String, id: String)

  object Decisions extends Table[DecisionDTO]("DECISIONS") {
    def userId = column[String]("USER_ID")
    def id = column[String]("ID", O.PrimaryKey)

    def owner = foreignKey("DEC", userId, Users)(_.id)

    def * = userId ~ id <> (DecisionDTO, DecisionDTO.unapply _)
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
    } catch {
      case ex: Exception => throw new RuntimeException("Unable to create tables for database " + session.metaData.getURL(), ex)
    }
  }
}
