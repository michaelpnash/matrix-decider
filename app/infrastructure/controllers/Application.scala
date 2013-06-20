package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import com.google.inject.{Inject, Singleton}
import domain.{User, UserRepository}

case class LoginView(login: String)

@Singleton
class Application @Inject()(userRepository: UserRepository) extends Controller {

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
          BadRequest(views.html.index(formWithErrors))
        },
        loginView => {
          val user = userRepository.findByName(loginView.login) match {
            case Some(user) => user
            case None => userRepository.save(User(loginView.login))
          }
          Redirect(routes.Decisions.list(user.id))
        }
      )
  }

}
