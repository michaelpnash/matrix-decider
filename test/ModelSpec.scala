import model._
import model.Alternative
import model.Criteria
import model.Decision
import model.User
import org.scalatest.FreeSpec

class ModelSpec extends FreeSpec {
  "a decision" - {
    "can produce a list of alternatives ordered by weighted ranking" in {
      val price = Criteria("price", 2)
      val color = Criteria("color", 1)
      val alternatives = Set(Alternative("ford", Set(Ranking(price, 4))), Alternative("gm", Set()))
      val decision = Decision(User("name"), alternatives, Set())
      val result = decision.alternativesByPreference
      assert(result === alternatives)
    }
    "can produce a list of criteria order by importance" in (pending)
  }
}
