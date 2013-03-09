package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import com.google.inject.{Inject, Singleton}

case class LoginView(login: String)

@Singleton
class Application @Inject()() extends Controller {

  private val loginForm: Form[LoginView] = Form(
    mapping(
      "username" -> nonEmptyText
    )(LoginView.apply)(LoginView.unapply)
  )

  def index = Action {
    implicit request =>
      Ok(views.html.index(loginForm))
  }

  def login = Action {
    implicit request =>
      loginForm.bindFromRequest.fold(
        formWithErrors => {
          println("Bad!")
          BadRequest(views.html.index(formWithErrors))
        },
        loginView => {
          println("ok!")
          Redirect(routes.Decisions.hello)
        }
      )
  }

}
