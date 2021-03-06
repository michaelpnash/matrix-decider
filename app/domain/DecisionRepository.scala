package domain

import java.util.UUID
import com.google.inject.{ Inject, Singleton }
import infrastructure.datastore.{ RankingDataStore, CriteriaDataStore, AlternativeDataStore, DecisionDataStore }
import infrastructure.datastore.Schema.{ RankingDTO, CriteriaDTO, AlternativeDTO, DecisionDTO }
import scala.slick.session.{ Database, Session }
import play.api.Logger
import javax.sql.DataSource

@Singleton
class DecisionRepository @Inject() (decisionDataStore: DecisionDataStore, alternativeDataStore: AlternativeDataStore,
  criteriaDataStore: CriteriaDataStore,
  rankingDataStore: RankingDataStore, userRepository: UserRepository, dataSource: DataSource) {

  val log = Logger.logger
  val database = Database.forDataSource(dataSource)

  def criteriaDomain(dto: CriteriaDTO) = Criteria(dto.name, dto.importance, dto.id)
  implicit def criteriaSet(dtoSet: Set[CriteriaDTO]) = dtoSet.map(criteriaDomain(_))
  def rankingDomain(dto: RankingDTO, criteria: Map[UUID, CriteriaDTO]) = Ranking(criteriaDomain(criteria(dto.criteriaId)), dto.rank)

  def findById(id: UUID): Option[Decision] = {
    database withSession { implicit session =>
      val criteria: Map[UUID, CriteriaDTO] = criteriaDataStore.findByDecisionId(id).map(dto => (dto.id, dto)).toMap
      val alternatives = alternativeDataStore.findByDecisionId(id).map(dto => {
        val rankings = criteriaDataStore.findByDecisionId(id).map(criteriaDto => rankingDataStore.findByAlternativeIdAndCriteriaId(dto.id, criteriaDto.id)).flatten

        Alternative(dto.name, rankings.map(rankingDomain(_, criteria)).toSet, dto.id)
      })
      decisionDataStore.findById(id).map(dto => Decision(userRepository.findById(dto.user).get, alternatives.toSet, criteria.values.map(criteriaDomain(_)).toSet, dto.id, dto.name))
    }
  }

  def save(decision: Decision): Decision = {
    database withTransaction { implicit session: Session =>
      decisionDataStore.insert(DecisionDTO(decision.user.id, decision.name, decision.id))
      decision.alternatives.foreach(alternative => {
        alternativeDataStore.insert(AlternativeDTO(alternative.name, decision.id, alternative.id))
        alternative.rankings.foreach(ranking => rankingDataStore.insert(RankingDTO(ranking.criteria.id, alternative.id, ranking.rank)))
      })
      decision.criteria.foreach(criteria => criteriaDataStore.insert(CriteriaDTO(criteria.name, criteria.importance, decision.id, criteria.id)))
    }
    decision
  }
  def decisionSpecifiersForUser(id: UUID): Seq[DecisionSpecifier] = database withSession { implicit session => decisionDataStore.findForUser(id).map(dto => DecisionSpecifier(dto.id, dto.name)) }

  def clear = database withSession { implicit session: Session => decisionDataStore.clear }

  def withNewAlternative(decision: Decision, alternative: Alternative): Decision = {
    database withTransaction { implicit session =>
      alternativeDataStore.insert(AlternativeDTO(alternative.name, decision.id, alternative.id))
      decision.copy(alternatives = decision.alternatives + alternative)
    }
  }

  def withNewCriteria(decision: Decision, criteria: Criteria): Decision = {
    database withTransaction { implicit session =>
      criteriaDataStore.insert(CriteriaDTO(criteria.name, criteria.importance, decision.id, criteria.id))
      decision.copy(criteria = decision.criteria + criteria)
    }
  }

  def withCriteriaImportance(decision: Decision, updatedCriteria: Criteria, i: Int): Decision = {
    log.info("Setting criteria importance for " + updatedCriteria.name + " to " + i)
    val existing = decision.criteria(updatedCriteria.id).get
    if (existing.importance != i) {
      database withTransaction { implicit session =>
        criteriaDataStore.updateImportance(updatedCriteria.id, i)
        decision.copy(criteria = decision.criteria.filter(_.id != existing.id) + existing.copy(importance = i))
      }
    } else decision
  }

  def withAlternativeRanked(decision: Decision, alternative: Alternative, criteria: Criteria, ranking: Int) = {
    log.info("Ranking alternative " + alternative.name + " on criteria " + criteria.name + " as " + ranking)
    database withTransaction { implicit session: Session =>
      decision.alternative(alternative.id).get.rankings.find(_.criteria.id == criteria.id) match {
        case None => rankingDataStore.insert(RankingDTO(criteria.id, alternative.id, ranking))
        case Some(existingRanking) => rankingDataStore.updateRanking(criteria.id, alternative.id, ranking)
      }
      decision.copy(alternatives = decision.alternatives.filter(_.id != alternative.id) + decision.alternative(alternative.id).get.copy(rankings = decision.alternative(alternative.id).get.rankings.filter(_.criteria.id != criteria.id) + Ranking(criteria, ranking)))
    }
  }

  def generateSampleData {
    val guestUser = userRepository.findByName("guest").getOrElse(userRepository.save(User("guest")))
    save(Decision(guestUser, Set(Alternative("Ford", Set()), Alternative("GM", Set()), Alternative("Toyota", Set())),
      Set(Criteria("Price", 5), Criteria("Fuel Economy", 3), Criteria("Warranty", 3)), name = "Buy a Car"))
  }

}
