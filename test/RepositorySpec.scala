import model.{UserRepository, DecisionRepository}
import org.scalatest.FreeSpec

class RepositorySpec extends FreeSpec {
  val repo = Global.injector.getInstance(classOf[DecisionRepository])
  val userRepo = Global.injector.getInstance(classOf[UserRepository])
  "the decision repository" - {
    "can create a new decision for a user" in (pending)
    "can retrieve a specific decision for a user" in (pending)
    "can update a decision" in (pending)
    "can retrieve a list of decisions for a user" in (pending)
    "can produce a set of sample data for a guest user" in {
      repo.generateSampleData
      val decisions = repo.decisionSpecifiersForUser(userRepo.findByName("guest").get.id)
      assert(decisions.size > 0)
      val decision = repo.findById(decisions.head.id).get
      assert(decision.alternatives.size > 0)
    }
  }
}
