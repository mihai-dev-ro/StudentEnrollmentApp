package student.repositories

import java.time.Instant

import common.models.{IdMetaModel, Property}
import common.repositories.{BaseRepo, IdTable}
import slick.dbio.DBIO
import slick.lifted.{Rep, Tag}
import student.models.{StudentApplication, StudentApplicationId, StudentApplicationMetaModel, StudentApplicationStatus, StudentApplicationVersion, StudentId}
import slick.jdbc.H2Profile.api.{DBIO => _, Rep => _, TableQuery => _, _}
import student.models.StudentApplicationMetaModel.ModelId
import student.models.StudentApplicationStatus.StudentApplicationStatus
import university.models.UniversityId

import scala.concurrent.ExecutionContext

class StudentApplicationRepo(implicit ec: ExecutionContext)
  extends BaseRepo[StudentApplicationId, StudentApplication, StudentApplicationTable] {

  def getAllByStudentId(studentId: StudentId): DBIO[Seq[StudentApplication]] = {
    require(studentId != null)

    query
      .filter(_.studentId === studentId)
      .result
  }

  override protected val mappingConstructor: Tag => StudentApplicationTable =
    new StudentApplicationTable(_)

  implicit protected val modelIdMapping: BaseColumnType[ModelId] =
    StudentApplicationId.studentApplicationIdDbMapping
  protected val metaModelToColumnsMapping: Map[Property[_], StudentApplicationTable => Rep[_]] =
    Map(StudentApplicationMetaModel.id -> (table => table.id),
      StudentApplicationMetaModel.studentId -> (table => table.studentId),
      StudentApplicationMetaModel.universityId -> (table => table.universityId)
  )
  override protected val metaModel: IdMetaModel = StudentApplicationMetaModel
}

class StudentApplicationTable(tag: Tag)
  extends IdTable[StudentApplicationId, StudentApplication](tag, "Student_applications") {

  def version: Rep[StudentApplicationVersion] = column[StudentApplicationVersion]("Version")
  def studentId: Rep[StudentId] = column[StudentId]("Student_id")
  def universityId: Rep[UniversityId] = column[UniversityId]("University_id")
  def status: Rep[StudentApplicationStatus] = column[StudentApplicationStatus]("Status")

  val academicRecords: Rep[String] = column[String]("Academic_records")
  val honorsAndDistinctions: Rep[String] = column[String]("Honors_and_distinctions")
  val volunteerActivities: Rep[String] = column[String]("Volunteer_activities")
  val otherInterests: Rep[String] = column[String]("Other_interests")
  val coverLetter: Rep[String] = column[String]("Cover_letter")
  val attachedFileUrl: Rep[String] = column[String]("Attached_file_url")

  def createdAt: Rep[Instant] = column("Created_at")
  def updatedAt: Rep[Instant] = column("Updated_at")

  override def * =
    (id, version, studentId, universityId, status, academicRecords.?, honorsAndDistinctions.?,
      volunteerActivities.?, otherInterests.?, coverLetter.?, attachedFileUrl.?,
      createdAt, updatedAt) <>
      ((StudentApplication.apply _).tupled, StudentApplication.unapply)

  implicit val statusDbMapping: BaseColumnType[StudentApplicationStatus] =
    MappedColumnType.base[StudentApplicationStatus, Int](
      st => st.id,
      intVal => StudentApplicationStatus(intVal)
    )
}