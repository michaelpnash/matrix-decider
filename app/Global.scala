import com.google.inject.{Binder, Module, Guice}
import play.api.GlobalSettings

object Global extends GlobalSettings {
  val injector = Guice.createInjector(new DeciderModule)

}

class DeciderModule extends Module {
  def configure(p1: Binder) {}
}