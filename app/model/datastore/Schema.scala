package model.datastore

import scala.slick.driver.HsqldbDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery}
import scala.slick.jdbc.StaticQuery.interpolation

import slick.session.Session
import collection.mutable.ListBuffer

object Schema {
  object Decisions extends Table[DecisionDTO]("DECISIONS") {
    def code = column[String]("CODE")

    def id = column[String]("ID", O.PrimaryKey)

    def * = code ~ id <> (DecisionDTO, DecisionDTO.unapply _)

    def idx = index("idx_decisions", (code), unique = true)
  }

  case class User(id: Option[Int], first: String, last: String)

  object Users extends Table[User]("users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def first = column[String]("first")
    def last = column[String]("last")
    def * = id.? ~ first ~ last <> (User, User.unapply _)
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
      if (!tableNames.contains(Decisions.tableName)) Decisions.ddl.create

    } catch {
      case ex: Exception => throw new RuntimeException("Unable to create tables for database " + session.metaData.getURL(), ex)
    }
  }
}
