package model

import java.util.UUID
import model.datastore.{RankingDataStore, AlternativeDataStore}
import model.datastore.Schema.AlternativeDTO
import scala.slick.session.Database

case class Decision(user: User, alternatives: Set[Alternative], criteria: Set[Criteria], id: UUID = UUID.randomUUID, name: String) {
  alternatives.foreach(alt => require(criteria == alt.rankings.map(_.criteria), "Alternative " + alt + " does not have the correct criteria"))
  def alternativesByPreference = alternatives //TODO
  def withNewAlternative(alternative: Alternative)(implicit alternativeDataStore: AlternativeDataStore, rankingDataStore: RankingDataStore, database: Database): Decision = {
    database.withTransaction { implicit session =>
      val newAlternative = alternative.copy(rankings = criteria.map(Ranking(_, 0)))
      alternativeDataStore.insert(AlternativeDTO(alternative.name, this.id, alternative.id))
      this.copy(alternatives = alternatives + newAlternative)
    }
  }
  def withNewCriteria(criteria: Criteria): Decision = this
}

case class Criteria(name: String, importance: Int, id: UUID = UUID.randomUUID)

case class Alternative(name: String, rankings: Set[Ranking], id: UUID = UUID.randomUUID)

case class Ranking(criteria: Criteria, rank: Int)

case class User(name: String, id: UUID = UUID.randomUUID)