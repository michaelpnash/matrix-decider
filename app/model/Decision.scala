package model

import java.util.UUID

case class Decision(user: User, alternatives: Set[Alternative], criteria: Set[Criteria], id: UUID = UUID.randomUUID, name: String) {

  def alternativesByPreference = alternatives //TODO

  def withNewAlternative(alternative: Alternative)(implicit decisionRepository: DecisionRepository): Decision = decisionRepository.withNewAlternative(this, alternative)

  def withNewCriteria(criteria: Criteria)(implicit decisionRepository: DecisionRepository): Decision = decisionRepository.withNewCriteria(this, criteria)
}

case class Criteria(name: String, importance: Int, id: UUID = UUID.randomUUID)

case class Alternative(name: String, rankings: Set[Ranking], id: UUID = UUID.randomUUID)

case class Ranking(criteria: Criteria, rank: Int)

case class User(name: String, id: UUID = UUID.randomUUID)