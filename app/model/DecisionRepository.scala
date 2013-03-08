package model

import java.util.UUID
import com.google.inject.{Inject, Singleton}
import model.datastore.{AlternativeDataStore, DecisionDataStore}
import model.datastore.Schema.{AlternativeDTO, DecisionDTO}
import scala.slick.session.Session

@Singleton
class DecisionRepository @Inject()(decisionDataStore: DecisionDataStore, alternativeDataStore: AlternativeDataStore, implicit val session: Session) {
  def findById(id: UUID): Option[Decision] = {
    val alternatives = alternativeDataStore.findByDecisionId(id.toString).map(_.asInstanceOf[AlternativeDTO]).map(dto => Alternative(dto.name, Set(), UUID.fromString(dto.id)))
    decisionDataStore.findById(id.toString).asInstanceOf[Option[DecisionDTO]].map(dto => Decision(User("foo"), alternatives.toSet, Set(), UUID.fromString(dto.id)))
  }
  def save(decision: Decision): Decision = {
    decisionDataStore.insert(DecisionDTO(decision.user.id.toString, decision.id.toString))
    decision
  }
  def decisionNamesForUser(id: UUID): List[String] = List()
  def clear = decisionDataStore.clear
}
