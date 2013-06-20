package infrastructure.datastore

import infrastructure.Global
import java.util.UUID
import infrastructure.datastore._
import infrastructure.datastore.Schema._
import org.scalatest.{BeforeAndAfter, FreeSpec}
import scala.slick.session.{Database, Session}

class RankingDataStoreTest extends FreeSpec with BeforeAndAfter {
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

  "The rankings data store" - {
    "should insert a ranking and retrieve it by alternative id and criteria id" in (pending)
    "should not insert a ranking for an invalid alternative id" in (pending)
    "should not insert a ranking for an invalid criteria id" in (pending)
  }
}
