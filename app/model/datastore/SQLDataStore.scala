package model.datastore

import slick.session._
import collection.mutable.ListBuffer
import slick.jdbc.StaticQuery

trait SQLDataStore[T <: {def id : String}] {

  val dbUrl = "jdbc:hsqldb:mem:test"

  implicit val session: scala.slick.session.Session =
    Database.forURL(dbUrl, driver = "org.hsqldb.jdbc.JDBCDriver").createSession

  def tables: List[String] = {
    val names = ListBuffer[String]()

    val meta = session.metaData.asInstanceOf[org.hsqldb.jdbc.JDBCDatabaseMetaData]
    val tables = meta.getTables(null, null, null, null)
    while (tables.next()) {
      val table = tables.getString("TABLE_NAME")
      names.append(table)
    }

    names.toList
  }

  def table: String

  def clear(implicit session: Session) = {
    StaticQuery.updateNA("delete from " + table).execute
  }

}
