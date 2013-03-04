
import model.datastore.DecisionDataStore
import org.scalatest.FreeSpec

class ModuleSpec extends FreeSpec {
  "the module" - {
    "produce a singleton for the decision data store" in {

      assert(Global.injector.getInstance(classOf[DecisionDataStore]) eq Global.injector.getInstance(classOf[DecisionDataStore]))

    }
  }
}
