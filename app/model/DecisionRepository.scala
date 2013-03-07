package model

import java.util.UUID

class DecisionRepository {
  def findById(id: UUID): Option[Decision] = None
  def save(decision: Decision): Decision = decision
  def decisionNamesForUser(id: UUID): List[String] = List()
}
