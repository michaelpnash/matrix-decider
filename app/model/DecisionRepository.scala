package model

import java.util.UUID
import com.google.inject.{Inject, Singleton}
import model.datastore.DecisionDataStore
import model.datastore.Schema.DecisionDTO
import scala.slick.session.Session

@Singleton
class DecisionRepository @Inject()(decisionDataStore: DecisionDataStore, implicit val session: Session) {
  def findById(id: UUID): Option[Decision] = {
    decisionDataStore.findById(id.toString).asInstanceOf[Option[DecisionDTO]].map(dto => Decision(User("foo"), Set(), Set(), UUID.fromString(dto.id)))
  }
  def save(decision: Decision): Decision = {
    decisionDataStore.insert(DecisionDTO(decision.user.id.toString, decision.id.toString))
    decision
  }
  def decisionNamesForUser(id: UUID): List[String] = List()
  def clear = decisionDataStore.clear
}
