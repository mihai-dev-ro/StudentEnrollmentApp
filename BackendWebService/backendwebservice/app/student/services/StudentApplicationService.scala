package student.services

import common.exceptions.ValidationException
import common.repositories.DateTimeProvider
import common.utils.DBIOUtils
import common.validations.PropertyViolation
import common.validations.constraints.NotNullViolation
import play.libs.F.Function3
import student.models._
import student.repositories.StudentApplicationRepo
import university.models.UniversityId
import student.services
import slick.dbio.DBIO
import student.validations.constraints.StudentApplicationAlreadySubmittedViolation

import scala.concurrent.ExecutionContext

class StudentApplicationService(
  studentApplicationValidator: StudentApplicationValidator,
  studentApplicationRepo: StudentApplicationRepo,
  dateTimeProvider: DateTimeProvider)(
  implicit ec: ExecutionContext
) {

  def startNewApplication(studentId: StudentId, universityId: UniversityId) = {
    require(studentId != null && universityId != null)

    val studentApplication = StudentApplication(
      StudentApplicationId(-1),
      StudentApplicationVersion(1),
      studentId,
      universityId,
      StudentApplicationStatus.Draft,
      None,
      None,
      None,
      None,
      None,
      None,
      dateTimeProvider.now,
      dateTimeProvider.now
    )

    studentApplicationRepo.insertAndGet(studentApplication)
  }

  def loadApplicationsForStudent(studentId: StudentId) = {
    require(studentId != null)

    studentApplicationRepo.getAllByStudentId(studentId)
  }

  def updateApplication(studentApplicationId: StudentApplicationId,
    studentApplicationForUpdating: StudentApplicationForUpdating) = {
    require(studentApplicationId != null && studentApplicationForUpdating != null)

    for {
      _ <- validate(studentApplicationForUpdating)
      studentApplicationValidated <- getStudentApplicationUpdated(studentApplicationId,
        studentApplicationForUpdating)
      _ <- validateStudentApplicationStatusForUpdate(studentApplicationValidated)
      studentApplicationValidatedAndVersionUpdated <- updateStudentApplicationVersion(studentApplicationValidated)
      studentApplication <- studentApplicationRepo.updateAndGet(studentApplicationValidatedAndVersionUpdated)
    } yield studentApplication
  }

  def validate(studentApplicationForUpdating: StudentApplicationForUpdating) = {
    DBIO.successful(
      studentApplicationValidator.validateStudentApplicationForUpdating(studentApplicationForUpdating)
    ).flatMap(violations =>
      DBIOUtils.fail(violations.isEmpty, new ValidationException(violations)))
  }

  def getStudentApplicationUpdated(studentApplicationId: StudentApplicationId,
    studentApplicationForUpdating: StudentApplicationForUpdating) = {

    def updateStudentApplication(studentApp: StudentApplication) = {
      val uAcademicRecords =
        studentApplicationForUpdating.academicRecords.orElse(studentApp.academicRecords)
      val uHonorsAndDistinctions =
        studentApplicationForUpdating.honorsAndDistinctions.orElse(studentApp.honorsAndDistinctions)
      val uVolunteerActivities =
        studentApplicationForUpdating.volunteerActivities.orElse(studentApp.volunteerActivities)
      val uOtherInterests =
        studentApplicationForUpdating.honorsAndDistinctions.orElse(studentApp.otherInterests)
      val uCoverLetter =
        studentApplicationForUpdating.honorsAndDistinctions.orElse(studentApp.coverLetter)

      studentApp.copy(
        academicRecords = uAcademicRecords,
        honorsAndDistinctions = uHonorsAndDistinctions,
        volunteerActivities = uVolunteerActivities,
        otherInterests = uOtherInterests,
        coverLetter = uCoverLetter,
        updatedAt = dateTimeProvider.now
      )
    }

    studentApplicationRepo.findById(studentApplicationId)
      .map(updateStudentApplication(_))
  }

  def validateStudentApplicationStatusForUpdate(studentApplication: StudentApplication) = {
    def validateStatus(status: StudentApplicationStatus.Value) = {
      if (status.equals(StudentApplicationStatus.Submitted))
        Seq(StudentApplicationAlreadySubmittedViolation)
      else Nil
    }.map(PropertyViolation("Status", _))

    DBIO.successful(validateStatus(studentApplication.status)).flatMap(violations =>
      DBIOUtils.fail(violations.isEmpty, new ValidationException(violations)))
  }

  def updateApplicationFile(studentApplicationId: StudentApplicationId,
    studentApplicationFile: StudentApplicationFile): Unit = {

    require(studentApplicationId != null && studentApplicationFile != null)

    for {
      _ <- validateStudentApplicationFile(studentApplicationFile)
      studentApplication <- studentApplicationRepo.findById(studentApplicationId)
      studentApplicationToUpdate = studentApplication.copy(
        attachedFileUrl = studentApplicationFile.attachedFileUrl,
        updatedAt = dateTimeProvider.now
      )
      studentApplicationToUpdateAndVersionUpdated <- updateStudentApplicationVersion(studentApplicationToUpdate)
      studentApplicationUpdated <- studentApplicationRepo.updateAndGet(studentApplicationToUpdateAndVersionUpdated)
    } yield studentApplicationUpdated
  }

  def validateStudentApplicationFile(studentApplicationFile: StudentApplicationFile) = {
    def validateFile(fileField: Option[String]) = {
      fileField.toRight(Seq(NotNullViolation)).fold(identity, _ => Seq.empty)
        .map(PropertyViolation("atasament", _))
    }

    DBIO.successful(validateFile(studentApplicationFile.attachedFileUrl)).flatMap(violations =>
      DBIOUtils.fail(violations.isEmpty, new ValidationException(violations)))
  }

  def submitApplication(studentApplicationId: StudentApplicationId)= {
    require(studentApplicationId != null)

    for {
      studentApplication <- studentApplicationRepo.findById(studentApplicationId)
      _ <- validateStudentApplicationStatusForUpdate(studentApplication)
      studentApplicationToUpdate = studentApplication.copy(
        status = StudentApplicationStatus.Submitted,
        updatedAt = dateTimeProvider.now
      )
      studentApplicationToUpdateAndVersionUpdated <- updateStudentApplicationVersion(studentApplicationToUpdate)
      studentApplicationUpdated <- studentApplicationRepo.updateAndGet(studentApplicationToUpdateAndVersionUpdated)
    } yield studentApplicationUpdated
  }

  def updateStudentApplicationVersion(studentApplication: StudentApplication) = {
    DBIO.successful(
      studentApplication
        .copy(version = StudentApplicationVersion(studentApplication.version.value + 1))
    )
  }
}