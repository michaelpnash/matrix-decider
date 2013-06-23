package infrastructure.datastore

import slick.session._
import collection.mutable.ListBuffer
import slick.jdbc.StaticQuery
import javax.sql.DataSource

trait SQLDataStore[T] {

  implicit def session(dataSource: DataSource): Session = Database.forDataSource(dataSource).createSession
  
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
