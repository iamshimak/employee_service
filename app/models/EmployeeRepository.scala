package models

import javax.inject.{Inject, Singleton}

import controllers.EmployeeCreateForm
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  private var employeeList = List(
    Employee(1, "Ahamed Shimak", Option("A"), "SE"),
    Employee(2, "Atheek Razzik", None, "BC"),
    Employee(3, "Nafath Nazeeh", Option("N"), "LW"),
    Employee(4, "Sachin Randale", None, "PM")
  )

//  def create(name: String, prefix: Option[String], role: String): Option[Employee] = {
//    val newID = employeeList.last.id + 1
//    employeeList = employeeList :+ Employee(newID, name, prefix, role)
//    get(newID)
//  }
//
//  def get(id: Long): Option[Employee] = {
//    employeeList.find(e => e.id == id)
//  }
//
//  def getAll: List[Employee] = {
//    employeeList
//  }

  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  private class EmployeeTable(tag: Tag) extends Table[Employee](tag, "employee") {
    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The age column */
    def prefix = column[Option[String]]("prefix")

    /** */
    def role = column[String]("role")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Person object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Person case classes
      * apply and unapply methods.
      */
    def * = (id, name, prefix, role) <> ((Employee.apply _).tupled, Employee.unapply)
  }


  private val employees = TableQuery[EmployeeTable]

  //TODO employee id should be auto-generated
  def create(employeeForm: EmployeeCreateForm): Future[String] = {
    val employee = Employee(999, employeeForm.name, employeeForm.prefix, employeeForm.role)
    dbConfig.db.run(employees += employee).map( _ => "Employee successfully added").recover {
      case ex: Exception => ex.getCause.getMessage
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(employees.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Employee]] = {
    dbConfig.db.run(employees.filter(_.id === id).result.headOption)
  }

  def getAll: Future[Seq[Employee]] = {
    dbConfig.db.run(employees.result)
  }

}