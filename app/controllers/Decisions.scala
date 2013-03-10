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
          log.info("Updated decision:" + newDecision)
          Ok(views.html.decision(newDecision, decisionForm))
        }
      )
  }
          //val newDecision = decisionRepository.findById(decisionId).get.withNewAlternative(Alternative(alternativeView.name, Set()))

  def edit(decisionId: UUID) = Action { implicit request =>
    Ok(views.html.decision(decisionRepository.findById(decisionId).get, decisionForm))
  }
}
