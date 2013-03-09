import model._
import model.Alternative
import model.Criteria
import model.datastore.Schema
import model.Decision
import model.User
import org.scalatest.{BeforeAndAfter, FreeSpec}

class ModelSpec extends FreeSpec with BeforeAndAfter {
  val repo = Global.injector.getInstance(classOf[DecisionRepository])
  val userRepo = Global.injector.getInstance(classOf[UserRepository])
  Schema.createTables(repo.session)

  before {
    repo.clear
    userRepo.clear
  }
  "a decision" - {
    "requires the same number of criteria for each alternative" in {
      val crit1 = Criteria("one", 1)
      val crit2 = Criteria("two", 2)
      intercept[Exception] {
        Decision(User("name"),
          Set(Alternative("one", Set(Ranking(crit1, 1)))), Set(crit1, crit2), name = "my decision")
      }
    }
    "can produce a list of alternatives ordered by weighted ranking" in {
      val price = Criteria("price", 2)
      val color = Criteria("color", 1)
      val alternatives = Set(Alternative("ford",
        Set(Ranking(price, 4), Ranking(color, 2))),
        Alternative("gm",
          Set(Ranking(price, 5), Ranking(color, 3))))
      val decision = Decision(User("name"), alternatives, Set(price, color), name = "my decision")
      val result = decision.alternativesByPreference
      assert(result === alternatives)
    }
    "can produce a list of criteria ordered by importance" in (pending)
  }
  "The user repository" - {
    "can do stuff" in (pending)
  }

  "The decision repository" - {

    "can store a decision and find it by it's identifier" in {
      val user = User("name")
      userRepo.save(user)
      val price = Criteria("price", 2)
      val color = Criteria("color", 1)
      val alternatives = Set(Alternative("ford",
        Set(Ranking(price, 4), Ranking(color, 2))),
        Alternative("gm",
          Set(Ranking(price, 5), Ranking(color, 3))))
      val decision = Decision(user, alternatives, Set(price, color), name = "a decision")
      val saved = repo.save(decision)
      assert(saved === decision)
      val retrieved = repo.findById(decision.id)
      assert(retrieved.isDefined)
      //assert(retrieved.get === decision)
    }
    "can produce a list of all decision names associated with a user" in {
      val user = User("name")
      userRepo.save(user)
      val decision: Decision = Decision(user, Set(), Set(), name = "a decision")
      repo.save(decision)
      val specs = repo.decisionSpecifiersForUser(user.id)
      assert(specs.head.id === decision.id)
      assert(specs.size === 1)
      assert(specs.head.name === decision.name)
    }
    "can persist a modified decision and retrieve it's modified form" in (pending)
  }
}
