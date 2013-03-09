import java.util.UUID
import model.UUIDConversions
import org.scalatest.FreeSpec
import UUIDConversions._

class UUIDConversionsSpec extends FreeSpec {
  def takesString(str: String) = str
  def takesUUID(uuid: UUID) = uuid

  "The UUID conversions" - {
    "converts from a String to a UUID implicitly" in {
      val string = UUID.randomUUID.toString
      assert(takesUUID(string) === UUID.fromString(string))
    }
    "converts from a UUID to a String implicitly" in {
      val uuid = UUID.randomUUID
      assert(takesString(uuid) === uuid.toString)
    }
    "throws an exception given an invalid UUID" in {
      intercept[Exception] { takesUUID("kaboom") }
    }
  }
}
