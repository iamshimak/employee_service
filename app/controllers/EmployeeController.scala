package controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.data.Forms._
import models.EmployeeRepository
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesAbstractController, MessagesControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

//TODO Make Repo & Controller Async
class EmployeeController @Inject()(repo: EmployeeRepository,
                                   cc: MessagesControllerComponents)
                                  (implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  private val employeeForm: Form[EmployeeCreateForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "prefix" -> optional(text),
      "role" -> nonEmptyText
    )(EmployeeCreateForm.apply)(EmployeeCreateForm.unapply)
  }

  def get(id: Long): Action[AnyContent] = Action.async { _ =>
    repo.get(id).map { employee =>
      Ok(Json.toJson(employee))
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
        repo.create(employeeForm).map { message =>
          Created(message)
        }
      }
    )
  }

  def getAll = Action.async { _ =>
    repo.getAll.map { employees =>
      Ok(Json.toJson(employees))
    }
  }

}

case class EmployeeCreateForm(name: String, prefix: Option[String], role: String)