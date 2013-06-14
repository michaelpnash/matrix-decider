package infrastructure.datastore

import infrastructure.Global
import java.util.UUID
import infrastructure.datastore._
import infrastructure.datastore.Schema._
import org.scalatest.{BeforeAndAfter, FreeSpec}
import scala.slick.session.{Database, Session}

class DecisionDataStoreTest extends FreeSpec with BeforeAndAfter {
  implicit val session = Global.injector.getInstance(classOf[Database]).createSession
  val userDataStore = Global.injector.getInstance(classOf[UserDataStore])
  val decisionDataStore = Global.injector.getInstance(classOf[DecisionDataStore])
  val criteriaDataStore = Global.injector.getInstance(classOf[CriteriaDataStore])
  val alternativeDataStore = Global.injector.getInstance(classOf[AlternativeDataStore])
  Schema.createTables

  before {
    alternativeDataStore.clear
    criteriaDataStore.clear
    decisionDataStore.clear
    userDataStore.clear
  }

  "The decision data store" - {
    "should insert and retrieve a decision DTO" in {
      val user = UserDTO(UUID.randomUUID, "name")
      userDataStore.insert(user)
      val dto = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(dto)
      val found = decisionDataStore.findById(dto.id)
      assert(found.get === dto)
    }
    "should throw an exception when asked to insert a decision for a user that does not exist" in {
      intercept[Exception] {
        decisionDataStore.insert(DecisionDTO(UUID.randomUUID, "name", UUID.randomUUID))
      }
    }
    "should throw an exception if attempting to update a decision that does not exist" in {
      val user = UserDTO(UUID.randomUUID, "name")
      userDataStore.insert(user)
      val dto = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(dto)
      intercept[Exception] {
        decisionDataStore.update(DecisionDTO(user.id, "other name", UUID.randomUUID))
      }
    }
     "should update a decision" in {
      val user = UserDTO(UUID.randomUUID, "name")
      userDataStore.insert(user)
      val dto = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(dto)
      val newDto = DecisionDTO(user.id, "other name", dto.id)
      decisionDataStore.update(newDto)
      assert(decisionDataStore.findById(newDto.id).get === newDto)
    }
    "should list all stored decision DTOs for a specified user id" in {
      val user = UserDTO(UUID.randomUUID, "name")
      val wrongUser = UserDTO(UUID.randomUUID, "wrong name")
      userDataStore.insert(user)
      userDataStore.insert(wrongUser)
      val dto1 = DecisionDTO(user.id, "name", UUID.randomUUID)
      val dto2 = DecisionDTO(user.id, "name", UUID.randomUUID)
      val wrongDto = DecisionDTO(wrongUser.id, "name", UUID.randomUUID)
      decisionDataStore.insert(dto1)
      decisionDataStore.insert(dto2)
      decisionDataStore.insert(wrongDto)
      assert(decisionDataStore.findForUser(user.id) === Seq(dto1, dto2))
    }
  }
}
