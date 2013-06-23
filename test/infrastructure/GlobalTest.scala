import controllers.Decisions
import infrastructure.datastore.DecisionDataStore
import domain.DecisionRepository
import org.scalatest.FreeSpec
import infrastructure.Global

class ModuleSpec extends FreeSpec {
  "the module" - {
    "produces a singleton for the decision data store" in {
      assert(Global.injector.getInstance(classOf[DecisionDataStore]) eq Global.injector.getInstance(classOf[DecisionDataStore]))
    }
    "produces a singleton for the decision repository" in {
      assert(Global.injector.getInstance(classOf[DecisionRepository]) eq Global.injector.getInstance(classOf[DecisionRepository]))
    }
    "produces a singleton for the decision controller" in {
      assert(Global.injector.getInstance(classOf[Decisions]) eq Global.injector.getInstance(classOf[Decisions]))
    }
  }
}

