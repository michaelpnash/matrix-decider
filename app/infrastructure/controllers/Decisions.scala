package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import domain._
import java.util.UUID

case class NewDecisionView(name: String)
case class DecisionView(alternativeName: Option[String], criteriaName: Option[String], criteriaImportance: Option[Int])

object Decisions {
  val importances = Map("0"->"Irrelevant", "1"->"Trivial", "2"->"Somewhat Important", "3"->"Important", "4"->"Very Important","5"->"Extremely Important").toSeq.sortBy(_._1)
  val rankings =  Map("0" -> "Not Ranked", "1" -> "Very Poor", "2" -> "Poor",
                              "3" -> "Medium", "4" -> "Good", "5" -> "Best").toSeq.sortBy(_._1)
}
@Singleton
class Decisions @Inject()(implicit val decisionRepository: DecisionRepository, userRepository: UserRepository) extends Controller {

  val log = Logger.logger

   private val newDecisionForm: Form[NewDecisionView] = Form(
    mapping(
      "name" -> nonEmptyText
    )(NewDecisionView.apply)(NewDecisionView.unapply)
  )

  private val decisionForm: Form[DecisionView] = Form(
    mapping(
      "alternativeName" -> optional(text),
      "criteriaName" -> optional(text),
      "criteriaImportance" -> optional(number)
    )(DecisionView.apply)(DecisionView.unapply)
  )

  def list(userId: UUID) = Action { implicit request =>
    Ok(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), newDecisionForm, userId))
  }

  def newDecision(userId: UUID) = Action { implicit request =>
    log.info("New decision created")
     newDecisionForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), formWithErrors, userId))
        },
        decisionView => {
          //save the new decision
          val user = userRepository.findById(userId).get
          decisionRepository.save(Decision(user, Set(), Set(), name = decisionView.name.capitalize))
          Ok(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), newDecisionForm, userId))
        }
      )
  }

  def update(decisionId: UUID) = Action { implicit request =>
    log.info("Decision updating")
    val decision = decisionRepository.findById(decisionId).get
    decisionForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.decision(decision, formWithErrors))
        },
        decisionView => {
          log.info("Update valid:" + decisionView)
          var newDecision = decisionRepository.findById(decisionId).get
          if (decisionView.alternativeName.isDefined) {
            log.info("Adding alternative " + decisionView.alternativeName.get)
            val rankings = request.body.asFormUrlEncoded.get.filter(_._1.startsWith("newalternativeranking_")).map(pair => newAlternativeRanking(pair._1, pair._2)).map(criteriaIdAndRank => Ranking(newDecision.criteria(criteriaIdAndRank._1).get, criteriaIdAndRank._2))
            newDecision = newDecision.withNewAlternative(Alternative(decisionView.alternativeName.get.capitalize, rankings.toSet))
          }
          if (decisionView.criteriaName.isDefined) newDecision = {
            log.info("Adding criteria " + decisionView.criteriaName.get)
            newDecision.withNewCriteria(Criteria(decisionView.criteriaName.get.capitalize, decisionView.criteriaImportance.getOrElse(0)))
          }
          newDecision = updateWithRankings(newDecision, request.body.asFormUrlEncoded.get)
          newDecision = updateWithImportances(newDecision, request.body.asFormUrlEncoded.get)
          log.info("Updated decision:" + newDecision)
          Ok(views.html.decision(newDecision, decisionForm))
        }
      )
  }

  private[this] def newAlternativeRanking(fieldName: String, fieldValue: Seq[String]): (UUID, Int) = {
    val pieces = fieldName.split('_')
    (UUID.fromString(pieces(1)), fieldValue.head.toInt)
  }

  private[this] def alternativeCriteriaAndRank(fieldName: String, fieldValue: Seq[String]) = {
    val pieces = fieldName.split('_')
    (UUID.fromString(pieces(1)), UUID.fromString(pieces(2)), fieldValue.head.toInt)
  }

  private[this] def updateWithRankings(decision: Decision, fields: Map[String, Seq[String]]) = {
    val ranks: List[(UUID, UUID, Int)] = fields.filter(_._1.startsWith("ranking_")).map(pair => alternativeCriteriaAndRank(pair._1, pair._2)).toList

    var newDecision = decision
    ranks.foreach(rank => newDecision = newDecision.withAlternativeRanked(decision.alternative(rank._1).get, decision.criteria(rank._2).get, rank._3))

    newDecision
  }

  private[this] def criteriaAndImportance(fieldName: String, fieldValue: Seq[String]) = {
    val pieces = fieldName.split('_')
    (UUID.fromString(pieces(1)), fieldValue.head.toInt)
  }

  private[this] def updateWithImportances(decision: Decision, fields: Map[String, Seq[String]]) = {
    val importances: List[(UUID, Int)] = fields.filter(_._1.startsWith("importance_")).map(pair => criteriaAndImportance(pair._1, pair._2)).toList
    var newDecision = decision
    importances.foreach(importance => newDecision = newDecision.withCriteriaImportance(decision.criteria(importance._1).get, importance._2))
    newDecision
  }


  def edit(decisionId: UUID) = Action { implicit request =>
    Ok(views.html.decision(decisionRepository.findById(decisionId).get, decisionForm))
  }
}
