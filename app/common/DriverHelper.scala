package common

import slick.jdbc.PostgresProfile
import PostgresProfile.api._

object DriverHelper {
  val db = Database.forConfig("default")
}