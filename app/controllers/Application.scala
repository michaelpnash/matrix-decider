package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}

case class LoginView(login: String)

@Singleton
class Application @Inject()() extends Controller {

  private val loginForm: Form[LoginView] = Form(
    mapping(
      "login" -> nonEmptyText
    )(LoginView.apply)(LoginView.unapply)
  )

  def index = Action {
    implicit request =>
    Ok(views.html.index(loginForm))
  }

}
