import model.{Alternative, User, Decision}
import org.scalatest.FreeSpec

class ModelSpec extends FreeSpec {
  "a decision" - {
    "can produce a list of alternatives ordered by weighted ranking" in {
      val alternatives = Set(Alternative("ford", Set()), Alternative("gm", Set()))
      val decision = Decision(User("name"), alternatives, Set())
      val result = decision.alternativesByPreference
      assert(result === alternatives)
    }
  }
}
