package model

import java.util.UUID
import com.google.inject.{Inject, Singleton}
import model.datastore.{RankingDataStore, CriteriaDataStore, AlternativeDataStore, DecisionDataStore}
import model.datastore.Schema.{RankingDTO, CriteriaDTO, AlternativeDTO, DecisionDTO}
import scala.slick.session.{Database, Session}
import play.api.Logger

@Singleton
class DecisionRepository @Inject()(decisionDataStore: DecisionDataStore, alternativeDataStore: AlternativeDataStore,
                                   criteriaDataStore: CriteriaDataStore,
                                   rankingDataStore: RankingDataStore, userRepository: UserRepository, database: Database) {

  implicit val session = database.createSession
  val log = Logger.logger

  def criteriaDomain(dto: CriteriaDTO) = Criteria(dto.name, dto.importance, dto.id)
  implicit def criteriaSet(dtoSet: Set[CriteriaDTO]) = dtoSet.map(criteriaDomain(_))
  def rankingDomain(dto: RankingDTO, criteria: Map[UUID, CriteriaDTO]) = Ranking(criteriaDomain(criteria(dto.criteriaId)), dto.rank)

  def findById(id: UUID): Option[Decision] = {
    val criteria:Map[UUID, CriteriaDTO] = criteriaDataStore.findByDecisionId(id).map(dto => (dto.id, dto)).toMap
    val alternatives = alternativeDataStore.findByDecisionId(id).map(dto => {
      val rankings = criteriaDataStore.findByDecisionId(id).map(criteriaDto => rankingDataStore.findByAlternativeIdAndCriteriaId(dto.id, criteriaDto.id)).flatten

      Alternative(dto.name, rankings.map(rankingDomain(_, criteria)).toSet, dto.id)
    })
    decisionDataStore.findById(id).map(dto => Decision(userRepository.findById(dto.user).get, alternatives.toSet, criteria.values.map(criteriaDomain(_)).toSet, dto.id, dto.name))
  }

  def save(decision: Decision): Decision = {
    database.withTransaction { implicit session: Session =>
      decisionDataStore.insert(DecisionDTO(decision.user.id, decision.name, decision.id))
      decision.alternatives.foreach(alternative => {
        alternativeDataStore.insert(AlternativeDTO(alternative.name, decision.id, alternative.id))
        alternative.rankings.foreach(ranking => rankingDataStore.insert(RankingDTO(ranking.criteria.id, alternative.id, ranking.rank)))
      })
      decision.criteria.foreach(criteria => criteriaDataStore.insert(CriteriaDTO(criteria.name, criteria.importance, decision.id, criteria.id)))
    }
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

  def withCriteriaImportance(decision: Decision, updatedCriteria: Criteria, i: Int): Decision = {
    log.info("Setting criteria importance for " + updatedCriteria.name + " to " + i)
    val existing = decision.criteria(updatedCriteria.id).get
    if (existing.importance != i) {
      criteriaDataStore.updateImportance(updatedCriteria.id, i)
      decision.copy(criteria = decision.criteria.filter(_.id != existing.id) + existing.copy(importance = i))
    } else decision
  }

  def withAlternativeRanked(decision: Decision, alternative: Alternative, criteria: Criteria, ranking: Int) = {
    log.info("Ranking alternative " + alternative.name + " on criteria " + criteria.name + " as " + ranking)
    decision.alternative(alternative.id).get.rankings.find(_.criteria.id == criteria.id) match {
      case None => rankingDataStore.insert(RankingDTO(criteria.id, alternative.id, ranking))
      case Some(existingRanking) => rankingDataStore.updateRanking(criteria.id, alternative.id, ranking)
    }
    decision.copy(alternatives = decision.alternatives.filter(_.id != alternative.id) + decision.alternative(alternative.id).get.copy(rankings = decision.alternative(alternative.id).get.rankings.filter(_.criteria.id != criteria.id) + Ranking(criteria, ranking)))
  }

}
