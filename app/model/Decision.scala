package model

import java.util.UUID

case class Decision(user: User, alternatives: Set[Alternative], criteria: Set[Criteria], id: UUID = UUID.randomUUID, name: String) {
  alternatives.foreach(alt => require(criteria == alt.rankings.map(_.criteria), "Alternative " + alt + " does not have the correct criteria"))
  def alternativesByPreference = alternatives //TODO
  def withNewAlternative(alternative: Alternative): Decision = this.copy(alternatives = alternatives + alternative.copy(rankings = criteria.map(Ranking(_, 0))))
  def withNewCriteria(criteria: Criteria): Decision = this
}

case class Criteria(name: String, importance: Int, id: UUID = UUID.randomUUID)

case class Alternative(name: String, rankings: Set[Ranking], id: UUID = UUID.randomUUID)

case class Ranking(criteria: Criteria, rank: Int)

case class User(name: String, id: UUID = UUID.randomUUID)