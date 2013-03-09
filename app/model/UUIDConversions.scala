package model

import java.util.UUID

object UUIDConversions {
  implicit def uuidToString(uuid: UUID) = uuid.toString
  implicit def stringToUUID(string: String) = UUID.fromString(string)
}
