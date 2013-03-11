package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import model._
import java.util.UUID
import model.Alternative
import model.Decision

case class NewDecisionView(name: String)
case class DecisionView(alternativeName: Option[String], criteriaName: Option[String], criteriaImportance: Option[Int])

object Decisions {
  val importances = Map("0"->"Irrelevant", "1"->"Trivial", "2"->"Somewhat Important", "3"->"Important", "4"->"Very Important","5"->"Extremely Important").toSeq.sortBy(_._1)
  val rankings =  Map("0" -> "Worst", "1" -> "Very Poor", "2" -> "Poor",
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
          if (decisionView.alternativeName.isDefined) newDecision = newDecision.withNewAlternative(Alternative(decisionView.alternativeName.get.capitalize, Set()))
          if (decisionView.criteriaName.isDefined) newDecision = newDecision.withNewCriteria(Criteria(decisionView.criteriaName.get.capitalize, decisionView.criteriaImportance.getOrElse(0)))
          newDecision = updateWithRankings(newDecision, request.body.asFormUrlEncoded.get)
          newDecision = updateWithImportances(newDecision, request.body.asFormUrlEncoded.get)
          log.info("Updated decision:" + newDecision)
          Ok(views.html.decision(newDecision, decisionForm))
        }
      )
  }

  private[this] def alternativeCriteriaAndRank(fieldName: String, fieldValue: Seq[String]) = {
    val pieces = fieldName.split('_')
    (UUID.fromString(pieces(1)), UUID.fromString(pieces(2)), fieldValue.head.toInt)
  }

  private[this] def updateWithRankings(decision: Decision, fields: Map[String, Seq[String]]) = {
    val ranks: List[(UUID, UUID, Int)] = fields.filter(_._1.startsWith("ranking_")).map(pair => alternativeCriteriaAndRank(pair._1, pair._2)).toList
    def findRankFor(alternativeId: UUID, criteriaId: UUID, default: Int): Int = ranks.find(p => p._1 == alternativeId && p._2 == criteriaId).getOrElse((alternativeId, criteriaId, default))._3
    val alternatives = decision.copy(alternatives = decision.alternatives.map(alternative => alternative.copy(rankings = alternative.rankings.map(ranking => ranking.copy(rank = findRankFor(alternative.id, ranking.criteria.id, ranking.rank)))))).alternatives
    decision.withModifiedAlternatives(alternatives.toList)
  }

  private[this] def updateWithImportances(decision: Decision, fields: Map[String, Seq[String]]) = {
    decision
  }


  def edit(decisionId: UUID) = Action { implicit request =>
    Ok(views.html.decision(decisionRepository.findById(decisionId).get, decisionForm))
  }
}
