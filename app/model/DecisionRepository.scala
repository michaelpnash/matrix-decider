package model

import java.util.UUID
import com.google.inject.{Inject, Singleton}
import model.datastore.{RankingDataStore, CriteriaDataStore, AlternativeDataStore, DecisionDataStore}
import model.datastore.Schema.{RankingDTO, CriteriaDTO, AlternativeDTO, DecisionDTO}
import scala.slick.session.{Database, Session}

@Singleton
class DecisionRepository @Inject()(decisionDataStore: DecisionDataStore, alternativeDataStore: AlternativeDataStore,
                                   criteriaDataStore: CriteriaDataStore,
                                   rankingDataStore: RankingDataStore, database: Database) {

  implicit val session = database.createSession

  def criteriaDomain(dto: CriteriaDTO) = Criteria(dto.name, dto.importance, dto.id)
  implicit def criteriaSet(dtoSet: Set[CriteriaDTO]) = dtoSet.map(criteriaDomain(_))
  def rankingDomain(dto: RankingDTO, criteria: Map[UUID, CriteriaDTO]) = Ranking(criteriaDomain(criteria(dto.criteriaId)), dto.rank)

  def findById(id: UUID): Option[Decision] = {
    val criteria:Map[UUID, CriteriaDTO] = criteriaDataStore.findByDecisionId(id).map(dto => (dto.id, dto)).toMap
    val alternatives = alternativeDataStore.findByDecisionId(id).map(_.asInstanceOf[AlternativeDTO]).map(dto => {
      val rankings = criteriaDataStore.findByDecisionId(id).map(criteriaDto => rankingDataStore.findByAlternativeIdAndCriteriaId(dto.id, criteriaDto.id)).flatten

      Alternative(dto.name, rankings.map(rankingDomain(_, criteria)).toSet, dto.id)
    })
    decisionDataStore.findById(id).asInstanceOf[Option[DecisionDTO]].map(dto => Decision(User("foo"), alternatives.toSet, criteria.values.map(criteriaDomain(_)).toSet, dto.id, dto.name))
  }
  def save(decision: Decision): Decision = {

    decisionDataStore.insert(DecisionDTO(decision.user.id, decision.name, decision.id))
    decision
  }
  def decisionSpecifiersForUser(id: UUID): Seq[DecisionSpecifier] = decisionDataStore.findForUser(id).map(dto => DecisionSpecifier(dto.id, dto.name))

  def clear = decisionDataStore.clear

  def withNewAlternative(decision: Decision, alternative: Alternative): Decision = {
    alternativeDataStore.insert(AlternativeDTO(alternative.name, decision.id, alternative.id))
    decision.copy(alternatives = decision.alternatives + alternative)
  }

  def withNewCriteria(decision: Decision, criteria: Criteria): Decision = {
    criteriaDataStore.insert(CriteriaDTO(criteria.name, criteria.importance, decision.id, criteria.id))
    decision.copy(criteria = decision.criteria + criteria)
  }
}
