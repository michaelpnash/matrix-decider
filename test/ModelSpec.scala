import model._
import model.Alternative
import model.Criteria
import model.Decision
import model.User
import org.scalatest.FreeSpec

class ModelSpec extends FreeSpec {
  "a decision" - {
    "requires the same number of criteria for each alternative" in {
      val crit1 = Criteria("one", 1)
      val crit2 = Criteria("two", 2)
      intercept[Exception] {
        Decision(User("name"),
          Set(Alternative("one", Set(Ranking(crit1, 1)))), Set(crit1, crit2))
      }
    }
    "can produce a list of alternatives ordered by weighted ranking" in {
      val price = Criteria("price", 2)
      val color = Criteria("color", 1)
      val alternatives = Set(Alternative("ford",
        Set(Ranking(price, 4), Ranking(color, 2))),
        Alternative("gm",
          Set(Ranking(price, 5), Ranking(color, 3))))
      val decision = Decision(User("name"), alternatives, Set(price, color))
      val result = decision.alternativesByPreference
      assert(result === alternatives)
    }
    "can produce a list of criteria ordered by importance" in (pending)
  }

  "The decision repository" - {
    val repo = Global.injector.getInstance(classOf[DecisionRepository])
    "can store a decision and find it by it's identifier" in {
      val price = Criteria("price", 2)
      val color = Criteria("color", 1)
      val alternatives = Set(Alternative("ford",
        Set(Ranking(price, 4), Ranking(color, 2))),
        Alternative("gm",
          Set(Ranking(price, 5), Ranking(color, 3))))
      val decision = Decision(User("name"), alternatives, Set(price, color))
      val saved = repo.save(decision)
      assert(saved === decision)
    }
    "can produce a list of all decision names associated with a user" in (pending)
    "can persist a modified decision and retrieve it's modified form" in (pending)
  }
}
