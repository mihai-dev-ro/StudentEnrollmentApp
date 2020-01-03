package student.repositories

import java.time.Instant

import authentication.models.SecurityUserId
import common.exceptions.MissingModelException
import common.models.{Email, IdMetaModel, Property}
import common.repositories._
import common.repositories.mappings.JavaTimeDbMappings
import common.utils.DBIOUtils
import student.models.{Student, StudentId, StudentMetaModel}
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api.{DBIO => _, MappedTo => _, Rep => _, TableQuery => _, _}
import slick.lifted.{ProvenShape, _}

import scala.concurrent.ExecutionContext

class StudentRepo(implicit private val ec: ExecutionContext)
  extends BaseRepo[StudentId, Student, StudentTable] {

  def findBySecurityUserIdOption(securityUserId: SecurityUserId): DBIO[Option[Student]] = {
    require(securityUserId != null)

    query
      .filter(_.securityUserId === securityUserId)
      .result
      .headOption
  }

  def findBySecurityUserId(securityUserId: SecurityUserId): DBIO[Student] = {
    findBySecurityUserIdOption(securityUserId)
      .flatMap(maybeStudent => DBIOUtils.optionToDBIO(maybeStudent,
        new MissingModelException(s"user with security user id $securityUserId")))
  }

  def findByEmailOption(email: Email): DBIO[Option[Student]] = {
    require(email != null)

    query
      .filter(_.email === email)
      .result
      .headOption
  }

  def findByEmail(email: Email): DBIO[Student] = {
    findByEmailOption(email)
      .flatMap(maybeStudent =>
        DBIOUtils.optionToDBIO(maybeStudent, new MissingModelException(s"user with email $email")))
  }

  override protected val mappingConstructor: Tag => StudentTable = new StudentTable(_)

  override protected val modelIdMapping: BaseColumnType[StudentId] = StudentId.studentIdDbMapping

  override protected val metaModel: IdMetaModel = StudentMetaModel

  override protected val metaModelToColumnsMapping: Map[Property[_], StudentTable => Rep[_]] = Map(
    StudentMetaModel.id -> (table => table.id)
  )
}

class StudentTable(tag: Tag) extends IdTable[StudentId, Student](tag, "Students")
  with JavaTimeDbMappings {

  def securityUserId: Rep[SecurityUserId] = column[SecurityUserId]("Security_user_id")

  def email: Rep[Email] = column[Email]("Email")

  def name: Rep[String] = column[String]("Name")

  def education: Rep[String] = column[String]("Education")

  def createdAt: Rep[Instant] = column("Created_at")

  def updatedAt: Rep[Instant] = column("Updated_at")

  def * : ProvenShape[Student] =
    (id, securityUserId, email, name.?, education.?, createdAt, updatedAt) <>
      ((Student.apply _).tupled, Student.unapply)
}