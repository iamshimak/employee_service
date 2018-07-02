package common

import slick.jdbc.PostgresProfile.api._

import scala.reflect._

trait BaseEntity {
  val id: Long
  val isDeleted: Boolean
}

trait WorkflowBaseEntity extends BaseEntity {
  val isApproved: Boolean
}

abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends Table[E](tag, schemaName, tableName) {

  val classOfEntity = classTag[E].runtimeClass
  val id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val isDeleted: Rep[Boolean] = column[Boolean]("is_deleted", O.Default(false))
}

abstract class WorkflowBaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String)
  extends BaseTable[E](tag, schemaName, tableName) {

  val isApproved: Rep[Boolean] = column[Boolean]("is_approved")
}