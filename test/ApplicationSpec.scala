package test

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.FreeSpec
import controllers.routes
import java.util.UUID

class ApplicationSpec extends FreeSpec {

  "Application" - {
    "when running" - {
      "should send 404 on a bad request" in {
        running(FakeApplication()) {
          (route(FakeRequest(GET, "/nowhere")) === None)
        }
      }
      "should render the index page" in {
        running(FakeApplication()) {
          val home = route(FakeRequest(GET, "/")).get

          assert(status(home) === OK)
          assert(contentType(home) === Some("text/html"))
          assert(contentAsString(home) contains ("Matrix Decider"))
        }
      }
      "should list decisions" in {
        running(FakeApplication()) {
          val home = route(FakeRequest(GET, routes.Decisions.list(UUID.randomUUID).url)).get
          assert(status(home) === OK)
          assert(contentType(home) === Some("text/html"))

        }
      }
    }
  }
}