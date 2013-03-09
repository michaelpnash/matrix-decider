package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import com.google.inject.{Inject, Singleton}
import model.UserRepository

case class LoginView(login: String)

@Singleton
class Application @Inject()(userRepository: UserRepository) extends Controller {

  private val loginForm: Form[LoginView] = Form(
    mapping(
      "username" -> nonEmptyText.verifying("No such user", userRepository.findByName(_).isEmpty)
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
          Redirect(routes.Decisions.list(userRepository.findByName(loginView.login).get.id))
        }
      )
  }

}
