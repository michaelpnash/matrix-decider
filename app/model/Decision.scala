package model

import java.util.UUID
import model.datastore.{RankingDataStore, AlternativeDataStore}
import model.datastore.Schema.{RankingDTO, AlternativeDTO}
import scala.slick.session.Database

case class Decision(user: User, alternatives: Set[Alternative], criteria: Set[Criteria], id: UUID = UUID.randomUUID, name: String) {

  def alternativesByPreference = alternatives //TODO

  def withNewAlternative(alternative: Alternative)(implicit alternativeDataStore: AlternativeDataStore,
                                                   database: Database): Decision = {
    database.withTransaction { implicit session =>
      alternativeDataStore.insert(AlternativeDTO(alternative.name, this.id, alternative.id))
      this.copy(alternatives = alternatives + alternative)
    }
  }
  def withNewCriteria(criteria: Criteria): Decision = this
}

case class Criteria(name: String, importance: Int, id: UUID = UUID.randomUUID)

case class Alternative(name: String, rankings: Set[Ranking], id: UUID = UUID.randomUUID)

case class Ranking(criteria: Criteria, rank: Int)

case class User(name: String, id: UUID = UUID.randomUUID)