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

@Singleton
class Decisions @Inject()(decisionRepository: DecisionRepository, userRepository: UserRepository) extends Controller {

   private val decisionForm: Form[DecisionView] = Form(
    mapping(
      "name" -> nonEmptyText
    )(DecisionView.apply)(DecisionView.unapply)
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

  def edit(decisionId: UUID) = Action { implicit request =>
    Ok(views.html.decision(decisionRepository.findById(decisionId).get))
  }
}
