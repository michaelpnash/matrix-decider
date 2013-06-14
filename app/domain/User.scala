package domain

import java.util.UUID

case class User(name: String, id: UUID = UUID.randomUUID)
