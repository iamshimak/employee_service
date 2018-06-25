package models

class EmployeeRepository {

  private var employeeList = List(
    Employee(1, "Ahamed Shimak", Option("A"), "SE"),
    Employee(2, "Atheek Razzik", None, "BC"),
    Employee(3, "Nafath Nazeeh", Option("N"), "LW"),
    Employee(4, "Sachin Randale", None, "PM")
  )

  def create(name: String, prefix: Option[String], role: String): Option[Employee] = {
    val newID = employeeList.last.id + 1
    employeeList = employeeList :+ Employee(newID, name, prefix, role)
    get(newID)
  }

  def get(id: Long): Option[Employee] = {
    employeeList.find(e => e.id == id)
  }

  def getAll: List[Employee] = {
    employeeList
  }

}
