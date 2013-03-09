package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import model.{UserRepository, Decision, DecisionRepository}
import java.util.UUID

case class DecisionView(name: String)
case class AlternativeView(name: String)
case class CriteriaView(name: String)

@Singleton
class Decisions @Inject()(decisionRepository: DecisionRepository, userRepository: UserRepository) extends Controller {

   private val decisionForm: Form[DecisionView] = Form(
    mapping(
      "name" -> nonEmptyText
    )(DecisionView.apply)(DecisionView.unapply)
  )

  private val alternativeForm: Form[AlternativeView] = Form(
    mapping(
      "alternativename" -> nonEmptyText
    )(AlternativeView.apply)(AlternativeView.unapply)
  )

  private val criteriaForm: Form[CriteriaView] = Form(
    mapping(
      "criterianame" -> nonEmptyText
    )(CriteriaView.apply)(CriteriaView.unapply)
  )

  def list(userId: UUID) = Action { implicit request =>
    Ok(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), decisionForm, userId))
  }

  def newDecision(userId: UUID) = Action { implicit request =>
     decisionForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), formWithErrors, userId))
        },
        decisionView => {
          //save the new decision
          val user = userRepository.findById(userId).get
          decisionRepository.save(Decision(user, Set(), Set(), name = decisionView.name))
          Ok(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), decisionForm, userId))
        }
      )
  }

  def newAlternative(decisionId: UUID) = Action { implicit request =>
    val decision = decisionRepository.findById(decisionId).get
    alternativeForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.decision(decision, formWithErrors, criteriaForm))
        },
        alternativeView => {
          //update decision
          val existing = decisionRepository.findById(decisionId).get
          decisionRepository.save(existing)
          Ok(views.html.decision(decision, alternativeForm, criteriaForm))
        }
      )
  }

   def newCriteria(decisionId: UUID) = Action { implicit request =>
    val decision = decisionRepository.findById(decisionId).get
    criteriaForm.bindFromRequest.fold(
        formWithErrors => {
          BadRequest(views.html.decision(decision, alternativeForm, formWithErrors))
        },
        alternativeView => {
          //update decision
          val existing = decisionRepository.findById(decisionId).get
          decisionRepository.save(existing)
          Ok(views.html.decision(decision, alternativeForm, criteriaForm))
        }
      )
  }

  def edit(decisionId: UUID) = Action { implicit request =>
    Ok(views.html.decision(decisionRepository.findById(decisionId).get, alternativeForm, criteriaForm))
  }
}
