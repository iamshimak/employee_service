package models

import common.BaseEntity
import play.api.libs.json.Json

case class Report(id: Long,
                  name: String,
                  employee_id: Long,
                  isDeleted: Boolean = false) extends BaseEntity

object Report {
  implicit val reads = Json.reads[Report]
  implicit val writes = Json.writes[Report]
}
