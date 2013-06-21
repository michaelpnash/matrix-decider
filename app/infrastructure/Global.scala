package infrastructure

import com.google.inject.{AbstractModule, Guice}
import domain.DecisionRepository
import datastore.Schema
import play.api.GlobalSettings
import scala.slick.session.{Database, Session}

object Global extends GlobalSettings {
  val injector = Guice.createInjector(new DeciderModule)
  Schema.createTables(injector.getInstance(classOf[Database]).createSession)
  injector.getInstance(classOf[DecisionRepository]).generateSampleData

  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }
}

class DeciderModule extends AbstractModule {
  val dbSuffix = System.getProperty("db", "mem:testdb")
  val dbUrl = "jdbc:hsqldb:" + dbSuffix

  import com.jolbox.bonecp.BoneCPDataSource
  
  val ds = new BoneCPDataSource
  ds.setDriverClass("org.hsqldb.jdbc.JDBCDriver")
  ds.setJdbcUrl(dbUrl)
  ds.setAcquireIncrement(5)

  override def configure() {
    bind(classOf[Database]).toInstance(Database.forDataSource(ds))
    bind(classOf[String]).toInstance("foo")
  }
}
