import com.google.inject.{AbstractModule, Guice}
import play.api.GlobalSettings
import scala.slick.session.{Session, Database}

object Global extends GlobalSettings {
  val injector = Guice.createInjector(new DeciderModule)

  override def getControllerInstance[A](clazz: Class[A]) = {
    injector.getInstance(clazz)
  }
}

class DeciderModule extends AbstractModule {
  val dbUrl = "jdbc:hsqldb:mem:testdb"

  override def configure() {
    bind(classOf[Session]).toInstance(Database.forURL(dbUrl, driver = "org.hsqldb.jdbc.JDBCDriver").createSession)
    bind(classOf[String]).toInstance("foo")
  }
}