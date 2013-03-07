
import model.datastore.DecisionDataStore
import model.DecisionRepository
import org.scalatest.FreeSpec

class ModuleSpec extends FreeSpec {
  "the module" - {
    "produce a singleton for the decision data store" in {
      assert(Global.injector.getInstance(classOf[DecisionDataStore]) eq Global.injector.getInstance(classOf[DecisionDataStore]))
    }
    "produxe a singleton for the decision repository" in {
      assert(Global.injector.getInstance(classOf[DecisionRepository]) eq Global.injector.getInstance(classOf[DecisionRepository]))
    }
  }
}
