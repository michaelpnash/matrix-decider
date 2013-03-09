package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.i18n.Messages
import com.google.inject.{Inject, Singleton}
import model.DecisionRepository

@Singleton
class Decisions @Inject()(decisionRepository: DecisionRepository) extends Controller {

  def hello = Action {
    Ok("hello")
  }

}
