package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}

@Singleton
class Application @Inject()() extends Controller {

  def index = Action {
    implicit request =>
    Ok(views.html.index())
  }

}
