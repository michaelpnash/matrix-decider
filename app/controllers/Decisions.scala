package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.nonEmptyText
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import model.DecisionRepository

@Singleton
class Decisions @Inject()() extends Controller {
  def list = Action {
    Ok("decisions show here")
  }
}
