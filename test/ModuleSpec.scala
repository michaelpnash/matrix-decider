
import model.datastore.DecisionDataStore
import org.specs2.mutable.Specification

class ModuleSpec extends Specification {
  "the module" should {
    "produce a singleton for the decision data store" in {
      Global.injector.getInstance(classOf[DecisionDataStore])
      0 must equalTo(0)
    }
  }
}
