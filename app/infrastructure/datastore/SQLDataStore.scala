package infrastructure.datastore

import slick.session._
import collection.mutable.ListBuffer
import slick.jdbc.StaticQuery

trait SQLDataStore[T] {

  def tables(implicit session: Session): List[String] = {
    val names = ListBuffer[String]()

    val meta = session.metaData.asInstanceOf[org.hsqldb.jdbc.JDBCDatabaseMetaData]
    val tables = meta.getTables(null, null, null, null)
    while (tables.next()) {
      val table = tables.getString("TABLE_NAME")
      names.append(table)
    }
    names.toList
  }

  def clear(implicit session: Session)

}
