import model.datastore.{DecisionDTO, DecisionDataStore}
import org.scalatest.FreeSpec

class DataStoreSpec extends FreeSpec {
  val dataStore = Global.injector.getInstance(classOf[DecisionDataStore])
  "The decision data store" - {
    "should insert and retrieve a decision DTO" in {
      val dto = DecisionDTO("foo", "bar")
      dataStore.insert(dto)
      val found = dataStore.findById(dto.id)
      assert(found.get === dto)
    }
    "should list all stored decision DTOs for a specified user id" in (pending)
  }
  "The user data store" - {
    "should insert and retrieve by id a new user DTO" in (pending)
    "shoult not insert a new user with the same name as an existing user" in (pending)
  }
}
