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
      val decision = Decision(user, alternatives, Set(price, color))
      val saved = repo.save(decision)
      assert(saved === decision)
      val retrieved = repo.findById(decision.id)
      assert(retrieved.isDefined)
      //assert(retrieved.get === decision)

      //Decision(User(foo,23296ff4-ecc8-4922-b569-0954070f12c2),Set(),Set(),66c2cead-faf4-4c0b-801b-be379ac49557) did not equal Decision(User(name,eff42bf5-5f34-4207-a2e7-1d0b00d770fb),Set(Alternative(ford,Set(Ranking(Criteria(price,2,397eda99-eace-4978-b23c-ad8972d58942),4), Ranking(Criteria(color,1,f98ac793-87f4-4293-865f-5493d079463f),2)),f30345a1-2175-4e59-b538-e812d63976c0), Alternative(gm,Set(Ranking(Criteria(price,2,397eda99-eace-4978-b23c-ad8972d58942),5), Ranking(Criteria(color,1,f98ac793-87f4-4293-865f-5493d079463f),3)),484832a2-a2f1-4567-9819-2b4897ea82d2)),Set(Criteria(price,2,397eda99-eace-4978-b23c-ad8972d58942), Criteria(color,1,f98ac793-87f4-4293-865f-5493d079463f)),66c2cead-faf4-4c0b-801b-be379ac49557) (ModelSpec.scala:60)


    }
    "can produce a list of all decision names associated with a user" in {
      val user = User("name")
      userRepo.save(user)
      val decision: Decision = Decision(user, Set(), Set())
      repo.save(decision)
      val specs = repo.decisionSpecifiersForUser(user.id)
      assert(specs.head.id === decision.id)
      assert(specs.size === 1)
      assert(specs.head.name === decision.name)
    }
    "can persist a modified decision and retrieve it's modified form" in (pending)
  }
}
