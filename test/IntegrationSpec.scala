import org.scalatest.FreeSpec
import play.api.test._
import play.api.test.Helpers._

class IntegrationSpec extends FreeSpec {
  "the application" - {
    "prompts for a login" in {
      running(TestServer(3333), HTMLUNIT) {
        browser =>
          browser.goTo("http://localhost:3333/")
          browser.waitUntil[Boolean] {
            browser.pageSource contains ("Log In")
          }
      }
    }
    "creates a user when a previously unknown user logs in" in {
      running(TestServer(3333), HTMLUNIT) {
        browser =>
          browser.goTo("http://localhost:3333/")
          browser.waitUntil[Boolean] {
            browser.pageSource contains ("Log In")
          }
          browser.$("#username").text("joe")
          browser.$("#submit").click()
          assert(browser.title() === "My Decisions")
      }
    }
  }
}
