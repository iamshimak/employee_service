package models

import common.BaseEntity
import play.api.libs.json._

case class Employee(id: Long,
                    name: String,
                    prefix: Option[String],
                    role: String,
                    isDeleted: Boolean = false) extends BaseEntity

object Employee {
  implicit val employeeReads = Json.reads[Employee]
  implicit val employeeWrites = Json.writes[Employee]
}