package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import model.DecisionRepository
import java.util.UUID

case class DecisionView(name: String)

@Singleton
class Decisions @Inject()(decisionRepository: DecisionRepository) extends Controller {

   private val decisionForm: Form[DecisionView] = Form(
    mapping(
      "name" -> nonEmptyText
    )(DecisionView.apply)(DecisionView.unapply)
  )

  def list(userId: UUID) = Action { implicit request =>
    Ok(views.html.decisions(decisionRepository.decisionSpecifiersForUser(userId), decisionForm))
  }

  def newDecision = Action {
    Ok("ok")
  }
}
