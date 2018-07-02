package tables

import common.BaseTable
import models.{Employee, Report}
import slick.jdbc.{JdbcProfile, PostgresProfile}
// TODO Load Profile from DriverHelper
object Tables extends {
  val profile: JdbcProfile = PostgresProfile
} with Tables

trait Tables {
  val profile: JdbcProfile

  import profile.api._

  class EmployeeTable(_tableTag: Tag) extends BaseTable[Employee](_tableTag, None, "employee") {
    override val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name")
    val prefix: Rep[Option[String]] = column[String]("prefix")
    val role: Rep[String] = column[String]("role")
    override val isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))
    def * = (id, name, prefix, role, isDeleted) <> ((Employee.apply _).tupled, Employee.unapply)

    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(prefix), Rep.Some(role), Rep.Some(isDeleted)).shaped. <> ({ r => import r._; _1.map(_ => (Employee.apply _).tupled((_1.get, _2.get, _3.get, _4.get, _5.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
  }

  lazy val employeeTable = new TableQuery(tag => new EmployeeTable(tag))

  class ReportTable(_tableTag: Tag) extends BaseTable[Report](_tableTag, None, "report") {
    val name: Rep[String] = column[String]("name")
    val employee_id: Rep[Long] = column[Long]("employee_id")
    val employee_fk = foreignKey("employee_fk", employee_id, employeeTable)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
    override def * = (id, name, employee_id, isDeleted) <> ((Report.apply _).tupled, Report.unapply)
  }
}