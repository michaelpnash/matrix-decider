package model

import java.util.UUID

case class Decision(user: User, alternatives: Set[Alternative], criteria: Set[Criteria], id: UUID = UUID.randomUUID, name: String) {

  def alternativesByPreference = alternatives.toList.sortBy(_.score * -1)

  def withNewAlternative(alternative: Alternative)(implicit decisionRepository: DecisionRepository): Decision = decisionRepository.withNewAlternative(this, alternative)

  def withNewCriteria(criteria: Criteria)(implicit decisionRepository: DecisionRepository): Decision = decisionRepository.withNewCriteria(this, criteria)

  val criteriaByImportance = criteria.toList.sortBy(_.importance)

  def withCriteriaImportance(criteria: Criteria, importance: Int)(implicit decisionRepository: DecisionRepository): Decision = decisionRepository.withCriteriaImportance(this, criteria, importance)

  def withAlternativeRanked(alternative: Alternative, criteria: Criteria, ranking: Int)(implicit decisionRepository: DecisionRepository): Decision = decisionRepository.withAlternativeRanked(this, alternative, criteria, ranking)

  def alternative(id: UUID) = alternatives.find(_.id == id)

  def criteria(id: UUID): Option[Criteria] = criteria.find(_.id == id)

  def ranking(alternativeId: UUID, criteriaId: UUID): Option[Int] = alternatives.find(_.id == alternativeId).map(_.ranking(criteriaId).getOrElse(0))

  def bestPossibleScore = criteria.filter(_.importance > 0).foldLeft(0)((max, criteria) => max + criteria.importance) * 5
}

case class Criteria(name: String, importance: Int, id: UUID = UUID.randomUUID)

case class Alternative(name: String, rankings: Set[Ranking], id: UUID = UUID.randomUUID) {
  def ranking(criteriaId: UUID): Option[Int] = rankings.find(_.criteria.id == criteriaId).map(_.rank)
  def score = rankings.foldLeft(0)((score, rank) => score + rank.criteria.importance * rank.rank)
}

case class Ranking(criteria: Criteria, rank: Int)

case class User(name: String, id: UUID = UUID.randomUUID)