package models

import play.api.libs.json._

case class Employee(id: Long,
                    name: String,
                    prefix: Option[String],
                    role: String)

object Employee {
  implicit val employeeReads = Json.reads[Employee]
  implicit val employeeWrites = Json.writes[Employee]
}