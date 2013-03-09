package model

import java.util.UUID
import com.google.inject.{Inject, Singleton}
import model.datastore.{RankingDataStore, CriteriaDataStore, AlternativeDataStore, DecisionDataStore}
import model.datastore.Schema.{RankingDTO, CriteriaDTO, AlternativeDTO, DecisionDTO}
import scala.slick.session.Session

@Singleton
class DecisionRepository @Inject()(decisionDataStore: DecisionDataStore, alternativeDataStore: AlternativeDataStore,
                                   criteriaDataStore: CriteriaDataStore,
                                   rankingDataStore: RankingDataStore, implicit val session: Session) {

  def criteriaDomain(dto: CriteriaDTO) = Criteria(dto.name, dto.importance, UUID.fromString(dto.id))
  implicit def criteriaSet(dtoSet: Set[CriteriaDTO]) = dtoSet.map(criteriaDomain(_))
  def rankingDomain(dto: RankingDTO, criteria: Map[String, CriteriaDTO]) = Ranking(criteriaDomain(criteria(dto.criteriaId)), dto.rank)

  def findById(id: UUID): Option[Decision] = {
    val criteria:Map[String, CriteriaDTO] = criteriaDataStore.findByDecisionId(id.toString).map(dto => (dto.id, dto)).toMap
    val alternatives = alternativeDataStore.findByDecisionId(id.toString).map(_.asInstanceOf[AlternativeDTO]).map(dto => {
      val rankings = criteriaDataStore.findByDecisionId(id.toString).map(criteriaDto => rankingDataStore.findByAlternativeIdAndCriteriaId(dto.id, criteriaDto.id)).flatten

      Alternative(dto.name, rankings.map(rankingDomain(_, criteria)).toSet, UUID.fromString(dto.id))
    })
    decisionDataStore.findById(id.toString).asInstanceOf[Option[DecisionDTO]].map(dto => Decision(User("foo"), alternatives.toSet, criteria.values.map(criteriaDomain(_)).toSet, UUID.fromString(dto.id)))
  }
  def save(decision: Decision): Decision = {
    decisionDataStore.insert(DecisionDTO(decision.user.id.toString, decision.id.toString))
    decision
  }
  def decisionNamesForUser(id: UUID): List[String] = List()
  def clear = decisionDataStore.clear
}
