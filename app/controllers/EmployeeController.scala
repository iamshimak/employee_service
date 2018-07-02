package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}

import models.Employee
import repos.{EmployeeRepository, ReportRepository}

import scala.concurrent.{ExecutionContext, Future}

class EmployeeController @Inject()(cc: MessagesControllerComponents)
                                  (implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  private val employeeRepo = new EmployeeRepository
  private val reportRepo = new ReportRepository

  private val employeeForm: Form[EmployeeCreateForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "prefix" -> optional(text),
      "role" -> nonEmptyText
    )(EmployeeCreateForm.apply)(EmployeeCreateForm.unapply)
  }

  def get(id: Long): Action[AnyContent] = Action.async { _ =>
    employeeRepo.getById(id).map { result =>
      result match {
        case None => NotFound("")
        case Some(e) => {
          reportRepo.getByEmployeeId(e.id).map { reportResult =>
            Ok(Json.obj(
              "id" -> e.id,
              "prefix" -> e.prefix,
              "role" -> e.role,
              "name" -> e.name,
              "reports" -> Json.toJson(reportResult)
            ))
          }
        }
      }
      Ok(Json.toJson(result))
    }
  }

  def create: Action[AnyContent] = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    employeeForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(BadRequest(errorForm.errorsAsJson))
      },
      // There were no errors in the from, so create the person.
      employeeForm => {
        val employee = Employee(0L, employeeForm.name, employeeForm.prefix, employeeForm.role)
        employeeRepo.save(employee).map { employee =>
          Created(Json.toJson(employee)).withHeaders("status" -> "employee created successfully")
        }
      }
    )
  }

  def getAll: Action[AnyContent] = Action.async { _ =>
    employeeRepo.getAll.map { employees =>
      Ok(Json.toJson(employees))
    }
  }

}

case class EmployeeCreateForm(name: String, prefix: Option[String], role: String)