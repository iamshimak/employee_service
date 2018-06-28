package models

import javax.inject.{Inject, Singleton}

import controllers.EmployeeCreateForm
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
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

  def create(employeeForm: EmployeeCreateForm): Future[Employee] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (employees.map(e => (e.name, e.prefix, e.role))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning employees.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((nameAge, id) => Employee(id, nameAge._1, nameAge._2, nameAge._3))
      // And finally, insert the person into the database
      ) += (employeeForm.name, employeeForm.prefix, employeeForm.role)
  }

  def delete(id: Long): Future[Int] = db.run {
    employees.filter(_.id === id).delete
  }

  def get(id: Long): Future[Option[Employee]] = db.run {
    employees.filter(_.id === id).result.headOption
  }

  def getAll: Future[Seq[Employee]] = db.run {
    employees.result
  }

}