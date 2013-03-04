import model.datastore.{DecisionDTO, DecisionDataStore}
import org.scalatest.FreeSpec

class DataStoreSpec extends FreeSpec {
  val dataStore = Global.injector.getInstance(classOf[DecisionDataStore])
  "The decision data store" - {
    "insert and retrieve a decision DTO" in {
      val dto = DecisionDTO("foo", "bar")
      dataStore.insert(dto)
      val found = dataStore.findById(dto.id)
      assert(found.get === dto)
    }
    "list all stored decision DTOs for a specified user" in (pending)
  }
}
