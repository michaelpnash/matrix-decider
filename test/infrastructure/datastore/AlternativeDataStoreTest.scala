package infrastructure.datastore

import infrastructure.Global
import java.util.UUID
import infrastructure.datastore._
import infrastructure.datastore.Schema._
import org.scalatest.{BeforeAndAfter, FreeSpec}
import scala.slick.session.{Database, Session}

class AlternativeDataStoreTest extends FreeSpec with BeforeAndAfter {
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

  "The alternative data store" - {
    "should insert and retrieve an alternative DTO by id" in {
      val user = UserDTO(UUID.randomUUID, "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(decisionDto)
      val alternative = AlternativeDTO("name", decisionDto.id, UUID.randomUUID)
      alternativeDataStore.insert(alternative)
      assert(alternativeDataStore.findById(alternative.id).get === alternative)
    }
    "should find all alternatives for a specified decision" in {
      val user = UserDTO(UUID.randomUUID, "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(decisionDto)
      val wrongDecision = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(wrongDecision)
      val alternative1 = AlternativeDTO("name 1", decisionDto.id, UUID.randomUUID)
      val alternative2 = AlternativeDTO("name 2", decisionDto.id, UUID.randomUUID)
      val wrongAlternative = AlternativeDTO("wrong", wrongDecision.id, UUID.randomUUID)
      List(alternative1, alternative2, wrongAlternative).foreach(alternativeDataStore.insert(_))
      assert(alternativeDataStore.findByDecisionId(decisionDto.id).toSet === Set(alternative1, alternative2))
    }
    "should throw an exception if we try to insert an alternative with a duplicate name in the same decision" in {
      val user = UserDTO(UUID.randomUUID, "name")
      userDataStore.insert(user)
      val decisionDto = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(decisionDto)
      val wrongDecision = DecisionDTO(user.id, "name", UUID.randomUUID)
      decisionDataStore.insert(wrongDecision)
      val alternative1 = AlternativeDTO("name 1", decisionDto.id, UUID.randomUUID)
      val alternative2 = AlternativeDTO(alternative1.name, decisionDto.id, UUID.randomUUID)
      alternativeDataStore.insert(alternative1)
      intercept[Exception] {
        alternativeDataStore.insert(alternative2)
      }
    }
  }
}
