import java.util.UUID
import model.datastore.{AlternativeDataStore, CriteriaDataStore, UserDataStore, DecisionDataStore}
import model.datastore.Schema.{AlternativeDTO, CriteriaDTO, UserDTO, DecisionDTO}
import org.scalatest.{BeforeAndAfter, FreeSpec}
import scala.slick.session.Session

class DataStoreSpec extends FreeSpec with BeforeAndAfter {
  implicit val session = Global.injector.getInstance(classOf[Session])
  val userDataStore = Global.injector.getInstance(classOf[UserDataStore])
  val decisionDataStore = Global.injector.getInstance(classOf[DecisionDataStore])
  val criteriaDataStore = Global.injector.getInstance(classOf[CriteriaDataStore])
  val alternativeDataStore = Global.injector.getInstance(classOf[AlternativeDataStore])

  before {
    alternativeDataStore.clear
    criteriaDataStore.clear
    decisionDataStore.clear
    userDataStore.clear
  }

  "The user data store" - {
    "should insert and retrieve by id a new user DTO" in {
      val user = UserDTO("id", "name")
      userDataStore.insert(user)
      assert(userDataStore.findById(user.id).get === user)
    }
    "should not insert a new user with the same name as an existing user" in {
      userDataStore.insert(UserDTO("id", "name"))
      intercept[Exception] {
        userDataStore.insert(UserDTO("id-other", "name"))
      }
    }
    "should find a user by name" in {
      val dto = UserDTO("id", "name")
      userDataStore.insert(dto)
      assert(userDataStore.findById(dto.name) === dto)
    }
  }
  "The alternative data store" - {
    "should insert and retrieve an alternative DTO by id" in {
      val user = UserDTO("foo", "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", "bar")
      decisionDataStore.insert(decisionDto)
      val alternative = AlternativeDTO("name", decisionDto.id, UUID.randomUUID.toString)
      alternativeDataStore.insert(alternative)
      assert(alternativeDataStore.findById(alternative.id).get === alternative)
    }
    "should find all alternatives for a specified decision" in {
      val user = UserDTO("foo", "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", "bar")
      decisionDataStore.insert(decisionDto)
      val wrongDecision = DecisionDTO(user.id, "name", "wrong")
      decisionDataStore.insert(wrongDecision)
      val alternative1 = AlternativeDTO("name 1", decisionDto.id, UUID.randomUUID.toString)
      val alternative2 = AlternativeDTO("name 2", decisionDto.id, UUID.randomUUID.toString)
      val wrongAlternative = AlternativeDTO("wrong", wrongDecision.id, UUID.randomUUID.toString)
      List(alternative1, alternative2, wrongAlternative).foreach(alternativeDataStore.insert(_))
      assert(alternativeDataStore.findByDecisionId(decisionDto.id).toSet === Set(alternative1, alternative2))
    }
  }
  "The criteria data store" - {
    "should insert and retrieve a criteria DTO" in {
      val user = UserDTO("foo", "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", "bar")
      decisionDataStore.insert(decisionDto)
      val criteria = CriteriaDTO("name", 1, decisionDto.id, UUID.randomUUID.toString)
      criteriaDataStore.insert(criteria)
      assert(criteriaDataStore.findById(criteria.id).get === criteria)
    }
    "should not insert a criteria for an invalid decision" in {
      intercept[Exception] {
        criteriaDataStore.insert(CriteriaDTO("name", 1, UUID.randomUUID.toString, UUID.randomUUID.toString))
      }
    }
    "should retrieve all applicable criteria given a decision id" in {
      val user = UserDTO("foo", "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", "bar")
      decisionDataStore.insert(decisionDto)
      val wrongDecision = DecisionDTO(user.id, "name", "baz")
      decisionDataStore.insert(wrongDecision)
      val criteria1 = CriteriaDTO("name 1", 1, decisionDto.id, UUID.randomUUID.toString)
      val criteria2 = CriteriaDTO("name 2", 1, decisionDto.id, UUID.randomUUID.toString)
      val wrongCriteria = CriteriaDTO("name", 1, wrongDecision.id, UUID.randomUUID.toString)
      criteriaDataStore.insert(criteria1)
      criteriaDataStore.insert(criteria2)
      criteriaDataStore.insert(wrongCriteria)
      assert(criteriaDataStore.findByDecisionId(decisionDto.id).toSet === Set(criteria1, criteria2))
    }
  }
  "The alternatives data store" - {
    "should insert an alternative and retrieve it by id" in (pending)
    "should find all alternatives applicable to a decision" in (pending)
    "should not insert an alternative for an invalid decision" in (pending)
  }
  "The rankings data store" - {
    "should do stuff" in (pending)
  }
  "The decision data store" - {
    "should insert and retrieve a decision DTO" in {
      val user = UserDTO("foo", "name")
      userDataStore.insert(user)
      val dto = DecisionDTO(user.id, "name", "bar")
      decisionDataStore.insert(dto)
      val found = decisionDataStore.findById(dto.id)
      assert(found.get === dto)
    }
    "should throw an exception when asked to insert a decision for a user that does not exist" in {
      intercept[Exception] {
        decisionDataStore.insert(DecisionDTO("not there", "name", "foo"))
      }
    }
    "should list all stored decision DTOs for a specified user id" in {
      val user = UserDTO("foo", "name")
      val wrongUser = UserDTO("wrong", "wrong name")
      userDataStore.insert(user)
      userDataStore.insert(wrongUser)
      val dto1 = DecisionDTO(user.id, "name", "foo")
      val dto2 = DecisionDTO(user.id, "name", "bar")
      val wrongDto = DecisionDTO(wrongUser.id, "name", "baz")
      decisionDataStore.insert(dto1)
      decisionDataStore.insert(dto2)
      decisionDataStore.insert(wrongDto)
      assert(decisionDataStore.findForUser(user.id) === Seq(dto1, dto2))
    }
  }
}
