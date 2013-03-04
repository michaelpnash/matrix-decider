import model.datastore.{UserDataStore, DecisionDataStore}
import model.datastore.Schema.{UserDTO, DecisionDTO}
import org.scalatest.{BeforeAndAfter, FreeSpec}
import scala.slick.session.Session

class DataStoreSpec extends FreeSpec with BeforeAndAfter {
  implicit val session = Global.injector.getInstance(classOf[Session])
  val userDataStore = Global.injector.getInstance(classOf[UserDataStore])
  val decisionDataStore = Global.injector.getInstance(classOf[DecisionDataStore])

  before {
    userDataStore.clear
    decisionDataStore.clear
  }

  "The user data store" - {
     "should insert and retrieve by id a new user DTO" in {
       val user = UserDTO("id", "name")
       userDataStore.insert(user)
       assert(userDataStore.findById(user.id).get === user)
     }
     "should not insert a new user with the same name as an existing user" in (pending)
   }
  "The decision data store" - {
    "should insert and retrieve a decision DTO" in {
      val user = UserDTO("foo", "name")
      userDataStore.insert(user)
      val dto = DecisionDTO(user.id, "bar")
      decisionDataStore.insert(dto)
      val found = decisionDataStore.findById(dto.id)
      assert(found.get === dto)
    }
    "should list all stored decision DTOs for a specified user id" in (pending)
  }

}
