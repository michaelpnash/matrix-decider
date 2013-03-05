import model.datastore.{UserDataStore, DecisionDataStore}
import model.datastore.Schema.{UserDTO, DecisionDTO}
import org.scalatest.{BeforeAndAfter, FreeSpec}
import scala.slick.session.Session

class DataStoreSpec extends FreeSpec with BeforeAndAfter {
  implicit val session = Global.injector.getInstance(classOf[Session])
  val userDataStore = Global.injector.getInstance(classOf[UserDataStore])
  val decisionDataStore = Global.injector.getInstance(classOf[DecisionDataStore])

  before {
    decisionDataStore.clear
    userDataStore.clear
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
    "should throw an exception when asked to insert a decision for a user that does not exist" in {
      intercept[Exception] { decisionDataStore.insert(DecisionDTO("not there", "foo")) }
    }
    "should list all stored decision DTOs for a specified user id" in {
      val user = UserDTO("foo", "name")
      val wrongUser = UserDTO("wrong", "name")
      userDataStore.insert(user)
      userDataStore.insert(wrongUser)
      val dto1 = DecisionDTO(user.id, "foo")
      val dto2 = DecisionDTO(user.id, "bar")
      val wrongDto = DecisionDTO(wrongUser.id, "baz")
      decisionDataStore.insert(dto1)
      decisionDataStore.insert(dto2)
      decisionDataStore.insert(wrongDto)
      assert(decisionDataStore.findForUser(user.id) === Seq(dto1, dto2))
    }
  }
}
