package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import models.EmployeeRepository
import play.api.libs.json.Json
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

//TODO Make Repo & Controller Async
class EmployeeController @Inject()(repo: EmployeeRepository,
                                   cc: MessagesControllerComponents)
                                  (implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  private val employeeForm: Form[EmployeeCreateForm] = Form {
    mapping(
      "emp_name" -> nonEmptyText,
      "emp_prefix" -> optional(text),
      "emp_role" -> nonEmptyText
    )(EmployeeCreateForm.apply)(EmployeeCreateForm.unapply)
  }

  def get(id: Long) = Action { request =>
    repo.get(id) match {
      case Some(employee) => Ok(Json.toJson(employee))
      case None => NotFound
    }
  }

  def create = Action { implicit request =>
    employeeForm.bindFromRequest.fold(
      errorForm => {
        BadRequest(errorForm.errorsAsJson)
      },
      employee => {
        repo.create(employee.name, employee.prefix, employee.role) match {
          case Some(e) => Created(Json.toJson(e))
          case None => BadRequest("Could Not Create Employee")
        }
      }
    )
  }

  def getAll() = Action { request =>
    Ok(Json.toJson(repo.getAll))
  }

}

case class EmployeeCreateForm(name: String, prefix: Option[String], role: String)